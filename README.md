# Service Novigrad Android Project
This is the repository of the Service Novigrad android project, made by SEG 2105 Project Group 1. 

The Service Novigrad application is an application designed to service a fictional province of Novigrad. This application handles three different users: the administrator, a Service Novigrad branch employee, and a customer. 

The administrator login is pre-programmed into the application, while the branch employee and customer accounts are stored in Firebase Firestore and handled by Firebase Authentication. The administrator manages all the possible services that can be offered by different Service Novigrad branches, and can add, edit, or delete a service. 

The branch employee account creates a branch profile for their branch, and selects the services offered by that branch, as well as the branch’s name, phone number, address, and working hours. The branch is notified when they have received a new request and can approve or reject the service request.

The customer can search for a Service Novigrad branch by its name, phone number, and address, and can filter by a given date and time as well as a desired service. The customer then makes a service request to the branch, providing the information and documents required by the service. They are notified when the branch has either approved or rejected their request. The customer can rate their experience with the Service Novigrad branch. 


# Group Members
Howard Hao En Tseng \
Feyi Adesanya \
Ethan Plant \
Zayd Ghazal

[![CircleCI](https://circleci.com/gh/SEG2105-uottawa/seg2105f20-project_gr-1.svg?style=svg&circle-token=f443aec0628521b9bce8be8de86effae4cdfc489)](https://app.circleci.com/pipelines/github/SEG2105-uottawa/seg2105f20-project_gr-1)


# Deliverable 1
The following functionality is implemented in deliverable 1:
- Logging into the application with an email and password using Firebase's Authentication API.
- Signing up for the application with name, email and password using Firebase's Authentication API.
- Creation of both Service Novigrad branch employee accounts and customer accounts.
- Storing user information using Cloud Firestore.
- Input validation on all login and sign up fields, and display of respective errors.
- Logging in as an admin account (username: "admin", password: "admin").
- Basic welcome screen after logging in displaying the user's name, email and role, as well as a sign out button.
- Remaining logged in after user has closed the app.

# Deliverable 2
The following functionality is implemented in deliverable 2:
- Integration with CircleCI for automated builds and automatic testing.
- Viewing a list of Employee and Customer accounts registered to the application.
- Deletion of Employee and Customer accounts.
- Adding, Editing, and Deleting services.

The Admin credentials are:
```
email: admin
password: admin
```

# Deliverable 3
The following functionality is implemented in deliverable 3:
- The ability to complete the employee branch profile information. 
    The following are mandatory fields:
    - Branch Name
    - Branch Phone Number
    - Branch Address
    - Branch Opening Time
    - Branch Closing Time
    - Days Branch is Open
    - Services Offered
- Adding services to branch profile from the list of services added by the admin.
- Deleting services from branch profile when no longer being offered.
- Specifying clinic working hours.
- Editing working hours.
- Approving and rejecting service requests submitted to the branch.
- Field validation for all fields.

The Province of Novigrad is assumed to be within the boundaries of Canada. Thus, the address searching query is restricted to Canada. 

The Sample Employee credentials are:
```
email: employee@novigrad.com
password: employee
```

# Deliverable 4
The following functionality is implemented in deliverable 4:
- Creating a new request to a given branch.
- Searching for branch by name, address, phone number, working hours, and types of services provided.
- Filling the required information and documents required by the service.
- Rating a branch by providing a comment and a rating from 1 to 5.
- Field validation for all fields.
- Customer receives a notification when their request is accepted/rejected.
- Employee receives a notification when a new request is made to their branch. (Additional feature)

The Sample Customer credentials are:
```
email: customer@novigrad.com
password: customer
```
