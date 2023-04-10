# HTS-ForceChangePassword
A Lambda function for helpdesk ticketing system that will set the user's password in place of temporary password after first time login.

# Responsibilities
- Take in `username`, `pasword` & `session_token` (token that the user has got after logging in for the first time. Then it check if username is valid and session_token has not expired, then it sets the new password; else sends appropriate response.

# Architecture Diagram

![Change password after first login](https://user-images.githubusercontent.com/63947196/230847922-29ff559c-40bf-42dd-b81d-64a888588663.jpg)
