package com.zyf.cfg

import org.babyfish.jimmer.sql.exception.SaveException.NotUnique
import org.babyfish.jimmer.sql.runtime.ExceptionTranslator
import org.springframework.stereotype.Component

@Component
class NotUniqueExceptionTranslator() : ExceptionTranslator<NotUnique> {
    override fun translate(ex: NotUnique, args: ExceptionTranslator.Args): Exception? {
//        if (ex.isMatched(Author::firstName, Author::lastName)) {
//            throw IllegalArgumentException(
//                ("The book with first name \"" +
//                    ex[Author::firstName] +
//                    "\" and last name " +
//                    ex[Author::lastName] +
//                    " already exists")
//            )
//        }
//        return null

            throw IllegalArgumentException(
                ("The unique already exists")
            )
    }
}

