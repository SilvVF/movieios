/*
Copyright 2015 Javier Tomás

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package io.silv.moviemp.database

import app.cash.paging.PagingSource
import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterBase
import io.silv.Database
import kotlinx.coroutines.flow.Flow

interface DatabaseHandler {
    suspend fun <T> await(inTransaction: Boolean = false, block: suspend Database.() -> T): T

    suspend fun <T : Any> awaitList(
        inTransaction: Boolean = false,
        block: suspend Database.() -> Query<T>,
    ): List<T>

    suspend fun <T : Any> awaitOne(
        inTransaction: Boolean = false,
        block: suspend Database.() -> Query<T>,
    ): T

    suspend fun <T : Any> awaitOneExecutable(
        inTransaction: Boolean = false,
        block: suspend Database.() -> ExecutableQuery<T>,
    ): T

    suspend fun <T : Any> awaitOneOrNull(
        inTransaction: Boolean = false,
        block: suspend Database.() -> Query<T>,
    ): T?

    suspend fun <T : Any> awaitOneOrNullExecutable(
        inTransaction: Boolean = false,
        block: suspend Database.() -> ExecutableQuery<T>,
    ): T?

    fun <T : Any> subscribeToList(block: Database.() -> Query<T>): Flow<List<T>>

    fun <T : Any> subscribeToOne(block: Database.() -> Query<T>): Flow<T>

    fun <T : Any> subscribeToOneOrNull(block: Database.() -> Query<T>): Flow<T?>

    fun <V : Any> queryPagingSource(
        countQuery: Database.() -> Query<Long>,
        transacter: Database.() -> TransacterBase,
        queryProvider: Database.(limit: Long, offset: Long) -> Query<V>,
        initialOffset: Long,
    ): PagingSource<Int, V>
}