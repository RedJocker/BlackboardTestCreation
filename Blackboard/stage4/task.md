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
    - retrieve student details for teacher student details
    - post grades from teacher student detail on click to submit button