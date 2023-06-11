

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