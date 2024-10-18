package com.renew.survey

import com.renew.survey.views.LoginActivity
import org.junit.Assert.*
import org.junit.Test

class LoginActivityTest{
    @Test
    fun istest(){
        val t = LoginActivity()
        assertEquals(true, t.testing())
    }
}