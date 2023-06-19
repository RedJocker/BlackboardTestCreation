# Description 

On this stage we will make the students screen




  - navigation
    - condition by Role
      - Role Student -> student screen
  - title (make it a component) 
    - make a separate file blackboard_title
    - include layout (include/merge tags)
        - view is going to be "shared"
          - same view id on different screens
        - binding routine changes
          - the component included get it's own binding, but it has to be initialized with a bind method call on the layout it is being included to
    - Student screen
      - title (include component)
      - student name
        - data from credential
      - test grades list (horizontal recycler view)
        - data from request
      - partial result
        - data calculated
      - exam grade
        - data from request
      - final grade
        - data calculated