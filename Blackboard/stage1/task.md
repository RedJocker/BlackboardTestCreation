# Description 

TODO(project level description)


Login screen with fake data coming from either from tests or default values, so its possible to
manually test the app by running it.

Password provided by tests as input is an encrypted version. 
To validate User plain password input perform the encryption and compare with test input

There will be two different user Roles, teacher and student, the plan for future stages is
to have different screens for those roles, providing different features. 
On this stage we will require just different Toast messages for valid logins based on the different
roles.

<br>

## Objetives
- MainActivity
  - id container
    - FragmentContainerView
    - one activity pattern, fragment based screens

- LoginFragment
  - blackboard_title
    - TextView
    - text: "::B L A C K B O A R D::"
  - login_username_et
    - EditText
    - hint: "username"
    - inputType: "textPersonName"
  - login_pass_et
    - EditText
    - hint: "password"
    - inputType: "textPassword"
  - login_submit_btn
    - Button
    - text: "SUBMIT"
    - on click do validate login
      - if valid
        - clear both login_username_et and login_pass_et
        - Toast message depending on Role
          - TEACHER
            - message: "Good day teacher [username]"
          - STUDENT
            - message: "Hello [username]"
      - else if invalid
        - clear password
        - set login_username_et error: "Invalid Login"
        - request focus on login_username_et
  - password encryption
    - plainPass to rawPassBytes -> toByteArray(StandardCharsets.UTF_8)
    - get instance sha-256 -> MessageDigest.getInstance("SHA-256")
    - make sha256HashPass -> messageDigest.digest(rawPassBytes)
    - encode with base64 -> android.util.Base64.encodeToString(
      sha256HashPass, android.util.Base64.NO_WRAP
      )

#Move to next stage
#TODO
  - title
    - make a separate file blackboard_title
    - include layout
      - view is going to be shared
      - binding routine changes
    - use title for network error messages
      - through error property
        - set focusable
  - create an application instance
    - manual dependency injection
  - network communication
    - /login
      - request
        - username
        - pass (encrypted version)
      - response
        - username
        - token
          - jwt token used to make request on endpoints that require authorization
        - role
  - navigation
    - condition by Role
      - Role Teacher -> teacher screen
      - Role Student -> student screen