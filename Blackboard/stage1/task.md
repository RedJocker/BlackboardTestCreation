# Description 
login screen with fake data coming from either from tests or default values, so its possible to
manually test the app by running it.

Password provided by tests as input is a encrypted version. 
To validate User plain password input perform the encryption and compare with test input

There will be two different user Roles, teacher and student, the plan for future stages is
to have different screens for those roles, providing different features. 
On this stage we will require just different Toast messages for valid logins based on the different
roles


## objetives
- MainActivity
  - id container
    - FragmentContainerView
    - one activity pattern, fragment based screens

- LoginFragment
  - title
    - make a separate file blackboard_title
    - include layout
      - view is going to be shared
      - binding routine changes
    - use title for network error messages
      - through error property
        - set focusable
  - login_submit_btn
    - on click validate login
      - if valid
        - clear fields
        - navigate depending on Role
      - else
        - if network error
          - TODO
        - if auth error
          - set error field on login_username_et
  - 
  - login_pass_et
    - 
  - login_username_et
  - password encryption


#Move to next stage

#TODO
  - create an application instance
    - manual dependency injection
  - network communication
    - /login
      - request
        - TODO
      - response
        - TODO
  - navigation
    - condition by Role
      - Role Teacher -> teacher screen
      - Role Student -> student screen
  
