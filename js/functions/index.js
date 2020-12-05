const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

//Deletes the user when the firestore document with the given user uid is deleted
exports.removeUser = functions.firestore
    .document("/users/{uid}")
    .onDelete((snapshot, context) => {
        const deleted = snapshot.data();

        return admin.auth().deleteUser(context.params.uid).then(() => {
            return admin.firestore().collection("branches").doc(context.params.uid).delete().catch(err => console.log(err));
        }).catch(err => console.log(err))
        .then(() => {
            return admin.firestore().collection("ratings").doc(context.params.uid).delete().catch(err => console.log(err));
        })
        .then(() => {
            return admin.firestore().collection("requests")
                .where('branch', '==', context.params.uid)
                .get().then(response => {
                    return response.docs.forEach(doc => {
                        admin.firestore().collection("requests").doc(doc.id).delete().catch(err => console.log(err));
                    })
                })
        }).catch(err => console.log(err));
    });

//Updates the service request with the branch, customer, and service names
exports.updateServiceRequest = functions.firestore
    .document("/requests/{id}")
    .onWrite((change, context) => {
        const after = change.after.data();

        return admin.firestore().collection("users").doc(after.customer).get().then(doc => {
            const data = doc.data();
            return admin.firestore().collection("requests").doc(context.params.id).update({"customerName": data.name});
        }).catch(err => console.log(err))
        .then(() => {
            return admin.firestore().collection("branches").doc(after.branch).get().then(doc => {
                const data = doc.data();
                return admin.firestore().collection("requests").doc(context.params.id).update({"branchName": data.name});
            }).catch(err => console.log(err))
            .then(() => {
                return admin.firestore().collection("services").doc(after.service).get().then(doc => {
                    const data = doc.data();
                    return admin.firestore().collection("requests").doc(context.params.id).update({"serviceName": data.name});
                }).catch(err => console.log(err))
            })
        })
    });

//Updates the service request branch name field when a branch is renamed
exports.updateBranchInfo = functions.firestore
    .document("branches/{id}")
    .onWrite((change, context) => {
        const after = change.after.data();
        return admin.firestore().collection("requests")
            .where('branch', '==', context.params.id)
            .get().then(response => {
                return response.docs.forEach(doc => {
                    admin.firestore().collection("requests").doc(doc.id).update({"branchName": after.name})
                }).catch(err => console.log(err));
        }).catch(err => console.log(err))
    });

//Calculates the branch rating when a new review is made to the branch
exports.calculateBranchRating = functions.firestore
    .document("ratings/{id}")
    .onWrite((change, context) => {
        const data = change.after.data();

        let count = 0, sum = 0;

        for (const key in data) {
            const value = data[key];
            sum += value;
            count += 1;
        }

        if(count !== 0) {
            let avg = sum / count;
            return admin.firestore().collection("branches").doc(context.params.id).update({"rating": avg}).catch(err => console.log(err))
        } else {
            return admin.firestore().collection("branches").doc(context.params.id).update({"rating": 0}).catch(err => console.log(err))
        }
    })

//Updates the service request service name when a service is renamed
exports.updateServiceNameInRequest = functions.firestore
    .document("services/{id}")
    .onWrite((change, context) => {
        const after = change.after.data();
        return admin.firestore().collection("requests")
            .where('service', '==', context.params.id)
            .get().then(response => {
                return response.docs.forEach(doc => {
                    admin.firestore().collection("requests").doc(doc.id).update({"serviceName": after.name})
                }).catch(err => console.log(err));
        }).catch(err => console.log(err))
    })

//Removes all the fields and requests that the deleted service was in
exports.deleteServices = functions.firestore
    .document("services/{id}")
    .onDelete((snap, context) => {
        return admin.firestore().collection("requests")
        .where('service', '==', context.params.id)
        .get().then(response => {
            return response.docs.forEach(doc => {
                admin.firestore().collection("requests").doc(doc.id).delete().catch(err => console.log(err));
            })
        }).then(() => {
            return admin.firestore().collection("branches")
            .where("services", "array-contains", context.params.id)
            .get().then(response => {
                return response.docs.forEach(doc => {
                    admin.firestore().collection("branches").doc(doc.id).update({"services": admin.firestore.FieldValue.arrayRemove(context.params.id)}).catch(err => console.log(err));
                })
            })
        }).catch(err => console.log(err));
    });

//Notifies the customer when their request is accepted/rejected.
exports.notifyCustomer = functions.firestore
    .document("requests/{id}")
    .onUpdate((change, context) => {
        const after = change.after.data();

        return admin.firestore().collection("users").doc(after.customer).collection("tokens").get().then(response => {
            return response.forEach(doc => {
                let token = doc.id;

                let payload;

                if(after.responded) {
                    if(after.approved) {
                        payload = {
                            notification: {
                                title: "Your request has been approved.",
                            }
                        }
                    } else {
                        payload = {
                            notification: {
                                title: "Your request has been rejected.",
                            }
                        }
                    }
                }
                return admin.messaging().sendToDevice(token, payload).catch(err => console.log(err));
            })
        }).catch(err => console.log(err));
    });

//Notifies the employee when a request has been made to their branch
exports.notifyEmployee = functions.firestore
    .document("requests/{id}")
    .onCreate((snap, context) => {
        const value = snap.data();

        return admin.firestore().collection("users").doc(value.branch).collection("tokens").get().then(response => {
            return response.forEach(doc => {
                let token = doc.id;

                let payload = {
                    notification: {
                        title: "A new request has been made.",
                    }
                };
                return admin.messaging().sendToDevice(token, payload).catch(err => console.log(err));
            })
        }).catch(err => console.log(err))
    });
