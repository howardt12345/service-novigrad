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