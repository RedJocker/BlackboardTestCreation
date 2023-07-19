# Description 

On this stage we will make the teacher screen




  - navigation
    - condition by Role
      - Role TEACHER -> teacher screen
  - title (make it a component)
    - include layout (include/merge tags)
        - view is going to be "shared"
          - same view id on different screens
        - binding routine changes
          - the component included get it's own binding, but it has to be initialized with a bind method call on the layout it is being included to
    - Teacher screen
      - title (include component)
      - list of student
        - data from request
    - Student details screen
      - re-use student screen
      - add button
      - data from request