# Description 

On this stage we will replace the local login authentication and use a network service to handle
login authentication



## Objetives
  - add permission on manifest file
    - internet
  - title
    - use title for network error messages
      - through error property
        - set focusable
  - create an application instance
    - manual dependency injection
    - add application on manifest file
  - network communication
    - set baseUrl
      - test pass baseUrl as input
      - if test has not passed baseUrl use a default that is convenient for you to do manual testing
      - there is a special address to access your computer localhost from the device you are testing
        - http://10.0.2.2:3001/
          - this address will access port 3001 on your localhost, you can change the port to the port you need
    - set up Mockoon for manual testing endpoints
      - Mockoon mocks a backend server so that you can develop the frontend
      - mock backend can be shared through json files
        - this project has a Blackboard.json file with a mock backend server for this project
        - on Mockoon go to File -> Open environment and choose Blackboard.json
        - Blackboard.json has set the base url to http://localhost:3001/, but you can change it selecting the mock sever Blackboard and clicking on configurations
    - /login
      - request
        - username
        - pass (encrypted version)
      - 200 response
        - username
        - token
          - jwt token used to make request on endpoints that require authorization
        - role
      - 401 
        - set login_username_et error to <code>"Invalid Login"</code>
      - other status 
        - set blackboard_title error to the response message.

# to next stage
  - navigation
    - condition by Role
      - Role Teacher -> teacher screen
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