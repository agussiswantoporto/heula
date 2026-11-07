package id.heula.auth

import kotlin.JsFun

@JsFun("() => { if (window.heulaGoogleAuth) { window.heulaGoogleAuth.signIn(); } }")
external fun triggerWebGoogleSignIn()

@JsFun("() => { if (window.heulaGoogleAuth) { window.heulaGoogleAuth.signOut(); } }")
external fun triggerWebGoogleSignOut()

@JsFun("() => { return window.localStorage.getItem('heula_google_auth_result') || ''; }")
external fun getWebGoogleAuthResult(): String

@JsFun("() => { window.localStorage.removeItem('heula_google_auth_result'); }")
external fun clearWebGoogleAuthResult()
