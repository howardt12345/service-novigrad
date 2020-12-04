# Service Novigrad Android Project
This is the repository of the Service Novigrad android project, made by SEG 2105 Project Group 1. 

# Group Members
Howard Hao En Tseng (300108234) \
Feyi Adesanya (300120992) \
Ethan Plant (300109850) \
Zayd Ghazal (300112270)

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