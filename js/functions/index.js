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
        return admin.auth().deleteUser(context.params.uid);
    });

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
