package com.eat.jani.models.auth

data class IsAvailableResponce(
    val isAvailable: Boolean,
    val message: String
)

data class MessageResponce(
    val message: String
)

data class LoginResponce(
    val status: String,
    val information: UserInfo
)
data class UserInfo(
    val username: String,
    val email: String,
    val token: String,
    val expirationTime: String,
    val refreshToken: String,
    val refreshTokenExpirationDateTime: String
)
data class UserResponce(
    val email: String,
    val username: String
)

/* Responses
    // register
    {
      "message": "Registration successful. Please verify your email to activate your account.",
      "email": "asaturov.nika@gmail.com",
      "username": "supera53",
      "isVerified": false
    }
    // check-email
    {
      "isAvailable": false,
      "message": "Email is already in use."
    }
    // check-username
    {
      "isAvailable": true,
      "message": "supera53 is available"
    }
    // send code
    {
      "message": "Verification email sent successfully"
    }
    // verify
    response.body()
    // email send for password restoration
    {
      "message": "Verification email sent successfully"
    }
    // password reset
    {
      "message": "Password has been successfully reset."
    }
    // login
    {
      "status": "Success",
      "information": {
        "username": "supera53",
        "email": "asaturov.nika@gmail.com",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxYzc3M2MyMi1iNjBiLTQ0MjAtZjA1Mi0wOGRkN2RkYmIwNDAiLCJqdGkiOiJmNmY2MTEzZC00OTUyLTRkZDMtYmYxZC01NmI2N2YwYjU1ZGMiLCJpYXQiOiIxNzQ0OTE0NjI1IiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvZW1haWxhZGRyZXNzIjoiYXNhdHVyb3YubmlrYUBnbWFpbC5jb20iLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoic3VwZXJhNTMiLCJleHAiOjE3NDQ5MTUyMjUsImlzcyI6Imh0dHBzOi8vbG9jYWxob3N0OjcyMjkiLCJhdWQiOiJKYW5pQXBwQ2xpZW50In0.PsW1KY-j3VxcGvt4Nma0-WE5rcHBa5hqO4kaPQtwNYI",
        "expirationTime": "2025-04-17T18:40:25.1831097Z",
        "refreshToken": "xl/cQL1uaQnrUfuuM6lFtb7+QoFVcF7z5BatI0wQLVJOFfKFAuY+I+9KRhqf9Ai86lTtR0uiK6aL99jr/79Dog==",
        "refreshTokenExpirationDateTime": "2025-05-17T18:30:25.2054324Z"
      },
      "redirectUrl": null
    }
    // generate tokens
    {
      "username": "supera53",
      "email": "asaturov.nika@gmail.com",
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxYzc3M2MyMi1iNjBiLTQ0MjAtZjA1Mi0wOGRkN2RkYmIwNDAiLCJqdGkiOiIyYmQzOWZkNy0wOTc4LTQyMDMtOWZjYy1iZjZjYjU3MWQ0ZTUiLCJpYXQiOiIxNzQ0OTE1MjIwIiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvZW1haWxhZGRyZXNzIjoiYXNhdHVyb3YubmlrYUBnbWFpbC5jb20iLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoic3VwZXJhNTMiLCJleHAiOjE3NDQ5MTU4MjAsImlzcyI6Imh0dHBzOi8vbG9jYWxob3N0OjcyMjkiLCJhdWQiOiJKYW5pQXBwQ2xpZW50In0.SH77LGi7egVjGD03AhNhMLvzOZGetoR2svI9NnRuHKE",
      "expirationTime": "2025-04-17T18:50:20.1526342Z",
      "refreshToken": "kprQOf3cSpRJDXhfmTOQLOnStMugtuQA7YZBm8Vm+LbvBTCliJfXIHuF0Rg5JP7NEaOw/JOcv2e2YR0RYc7o0w==",
      "refreshTokenExpirationDateTime": "2025-05-17T18:40:20.1536591Z"
    }
*/
