package com.renew.survey.helper.compressor.constraint


class Compression {
    internal val constraints: MutableList<Constraint> = mutableListOf()

    fun constraint(constraint: Constraint) {
        constraints.add(constraint)
    }
}