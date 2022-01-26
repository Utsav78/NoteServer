package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.data.model.User
import com.example.repository.DatabaseFactory
import com.example.repository.repo
import com.example.routes.userRoutes
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.locations.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    DatabaseFactory.init()
    val db = repo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }




    install(Authentication) {
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(Locations)

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        userRoutes(db, jwtService, hashFunction)



        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }

        //localhost:8080/notes
        post("/notes") {
            val body = call.receive<String>()
            call.respond(body)

        }

        delete("/notes") {
            val body = call.receive<String>()
            call.respond(body)

        }

        get("/token") {
            val email = call.request.queryParameters["email"]!!
            val password = call.request.queryParameters["password"]!!
            val username = call.request.queryParameters["username"]!!

            val user = User(email, hashFunction(password), username)
            call.respond(jwtService.generateToken(user))

        }
    }
}

