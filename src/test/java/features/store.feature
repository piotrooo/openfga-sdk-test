Feature: Handle store actions
  Background:
    * url sdkAppBaseUrl

  Scenario: Create a new store
    * def requestBody =
      """
      {
        "name": "sample-store"
      }
      """
    * path '/stores'
    * request requestBody
    * method post
    * status 201
    * match response.id == '#string'
    * match response.name == 'sample-store'
