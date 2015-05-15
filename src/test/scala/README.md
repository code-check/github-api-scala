# Tests
This readme documents the changes to the tests.

## Initial Setup
1. Change variable user in Constants.scala to your own Github username.
2. If you have no yet exported your Github token, create one [here](https://github.com/settings/tokens) and export it.

``` bash
export GITHUB_TOKEN=[Your GitHub Token] 
```

## Optional settings
- showResponse 
-- Set this to true if you would like to see the response JSON data. Otherwise it is omitted when running tests.

The following variables are for futureproofing. Generally won't need to be modified.
- otherUser
-- Another user's (not yourself) username.
- otherUserInvalid
-- An invalid username.
- organizationInvalid
-- An invalid organization.
- repoInvalid
-- An invalid repo.

The following variables should not be changed.
- organization
-- This is by default set to our dummy test organization "celestialbeings".
- repo
-- This is by default set to the dummy est repo "test-repo".

## Random Generator
The random string generator is located in Constants.scala and uses words from the wordBank array. It has three methods.
- generateRandomString()
-- Returns a String with three random words seperated by spaces.
- generateRandomWord()
-- Returns a String with a single random word.
- generatedRandomInt()
-- Returns a random Int from 0 to 999. This was added to avoid having to import the Random class in every file (so it is bundled with Constants)
Use these to generate random field values to test create and update functions.