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
          - server response might contain negative values that represent grades that were not yet assigned
            - in case exam grade is not a negative value assign zero to all negative test grades
            - in case of negative value on test grades display an empty string instead of the value (if the exam grade is also a negative value)
      - partial result
        - data calculated
        - simple arithmetic average from test grades ((total sum of test grades) / (total num of test grades))
          -  negative values should not be summed up on the total, but the total num of test grades still count that test
            - ex [100, -1, 100, -1] -> partial result = 200 / 4 = 50
        - student_partial_result_tv
          - TextView
      - exam grade
        - data from request
          - server response might contain a negative value that represent exam grades that was not yet assigned
            - in case of negative value display an empty string instead of the value
      - final grade
        - data calculated
          - only display value when the final grade is really final
            - if there is no remaining test grades to be done and exam is not possible
            - if there is already a exam grade
          - if the exam is not possible the final grade should be equal to the partial grade, even if there is a exam grade in server response
          - if the exam is possible the final grade should be the simple arithmetic average between partial grade and exam grade