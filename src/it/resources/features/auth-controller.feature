Feature: Valid users can get a token
  Scenario: client makes POST call to /token
    When the client calls token with valid username and password
    Then the client receives a valid token back

  Scenario: client with unauthorized IP address makes POST call to /token
    When the client calls token with unauthorized IP address
    Then the client receives NOT_A_CHANCE status code and 418 HTTP code

  Scenario: client with invalid IP address makes POST call to /token
    When the client calls token with invalid IPv4 address
    Then the client receives a bad request response

#  Scenario: client with invalid username or password