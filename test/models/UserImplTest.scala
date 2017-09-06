package models

import org.scalatest.Matchers._
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play._
import play.api.db.evolutions._
import play.api.db.Databases

class UserImplTest extends PlaySpec with BeforeAndAfterAll {
    val db = Databases(
        driver = "com.mysql.jdbc.Driver",
        url = "jdbc:mysql://localhost/test",
        config = Map(
            "user" -> "root"
        )
    )
    Evolutions.applyEvolutions(db)

    "UserImpl" should {
        val user = new UserImpl(db)

        val email = "test@example.com"
        val name = "name"
        val password = "password"
        val formal_flag = 0

        "create method" in {
            val result1 = user.create(email, name, password, formal_flag)

            result1 should not be empty
            result1.get should be (1)
        }

        "login method" in {
            val user_opt = user.login(name, password)
            val user_opt_empty = user.login("not_user", "not_password")

            user_opt should not be empty
            user_opt.get.id should be (1)

            user_opt_empty shouldBe empty
        }

        "update method" in {
            val new_user = User(1, name, email).copy(
                name = "new_name",
                email = "test_new@example.com"
            )
            val result = user.update(new_user)

            result should be (1)
        }

        "findByName method" in {
            val user_opt_empty = user.findByName(name)
            val user_opt = user.findByName("new_name")

            user_opt_empty shouldBe empty

            user_opt should not be empty
            user_opt.get.id should be (1)
        }

        "findById method" in {
            val user_opt = user.findById(1)

            user_opt should not be empty
            user_opt.get.id should be (1)
        }
    }

    override def afterAll(): Unit = {
        Evolutions.cleanupEvolutions(db)
    }
}