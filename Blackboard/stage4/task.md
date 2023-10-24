# Description

On this stage we will make the teacher screen


- navigation
    - condition by Role
        - Role TEACHER -> teacher screen
- TeacherFragment
    - list of students
- TeacherStudentDetailsFragment
    - title (make it a component)
        - make a separate file blackboard_title
        - include layout (include/merge tags)
            - view is going to be "shared"
                - same view id on different screens
            - binding routine changes
                - the component included get it's own binding, but it has to be initialized with a bind method call on the layout it is being included to
    - reuse student screen
        - make it a component
        - include layout (include/merge tags)
        - different logic
            - all edit text should be enabled
    - add a submit button
        - post grades to update grades on backend
- Teacher Client
    - retrieve all students for list on teacher fragment
      - /teacher/student
      - GET
      - reponse
        - 200 OK
        - { "students" : [{"name": "Lucas"}, ...] }
    - retrieve student details for teacher student details
      - /teacher/student/[name]
      - GET
      - response
        - 200 OK
        - {"name":"Lucas","grades":[-1,-1,-1,-1,-1,-1,-1],"exam":-1}
    - patch grades from teacher student detail on click to submit button
      - /teacher/student/[name]
      - PATCH
      - response
          - 200 OK
          - echoes request (may ignore)