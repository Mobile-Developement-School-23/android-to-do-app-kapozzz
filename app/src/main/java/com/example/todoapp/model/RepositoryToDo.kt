package com.example.todoapp.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.todoapp.database.ToDoDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Random

// Предоставляет view model необходимые данные
class RepositoryToDo private constructor(context: Context) {

    private val database by lazy { ToDoDB.getDatabase(context) }
    private val dao by lazy { database.mainDAO() }

    // Добавить дело
    suspend fun insertToDo(newToDo: TodoItem) {
        dao.insertToDo(newToDo)
    }

    // Удалить дело
    fun deleteToDo(id: String) {
        dao.deleteTodo(id)
    }

    // Обновить дело
    fun updateToDo(toDoItemToUpdate: TodoItem) {
        dao.updateToDoItem(toDoItemToUpdate)
    }

    // Все дела в livedata что-бы подписаться
    fun getToDoItems(): LiveData<List<TodoItem>?> {
        return dao.getToDoItems()
    }

    // Всегда будет один экземляр репозитория
    companion object {
        val repository: RepositoryToDo? = null
        fun getInstance(context: Context): RepositoryToDo =
            this.repository ?: RepositoryToDo(context)
    }

    fun generateTodoItems() {
        for (i in 1..20) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                insertToDo(generator())
            }
        }
    }

    private val random = Random()
    // Для генерации 20 заметок при первом входе
    private fun generator(): TodoItem {
        val randomValueForText = random.nextInt(20)
        val randomImportanceValue = random.nextInt(3)
        val notes = listOf(
            "Заметка об исчезновении волшебного артефакта из Запретного Замка.",
            "Таинственная записка о зловещих воинствах, скрывающихся в лесу.",
            "Пергамент с предсказанием о великом пророчестве, которое изменит ход истории.",
            "Заметка о тайной проходе в мир эльфов, расположенном за водопадом.",
            "Таинственная песнь сирен, затерянная в глубинах океана.",
            "Записка о волшебной роще, где ветер шепчет мудрые слова.",
            "Пергамент с инструкцией по созданию зелья бессмертия.",
            "Заметка о потерянном свите, способном разгонять тьму.",
            "Таинственное послание от дракона, пророчащее наступление битвы.",
            "Старый свиток с картой, ведущей к сокровищам королевства.",
            "Заметка о ритуале вызова духов древних воинов.",
            "Таинственный пергамент с заклинанием, способным открыть портал в другое измерение.",
            "Записка о забытом городе, погрязшем во времени и забвении.",
            "Пергамент с пророчеством о приближающемся конце света.",
            "Заметка о сокровищах эльфийских королей, спрятанных в подземельях.",
            "Таинственный свиток с запретным заклинанием, способным пробудить мертвых.",
            "Записка о мифической птице Феникс, воскрешающейся из пепла.",
            "Пергамент с легендой о волшебном мече, способном победить тьму.",
            "Заметка о загадочной истории магической амулетки, которая приносит удачу.",
            "Таинственная записка об артефакте, способном изменить прошлое и будущее."
        )
        val randomImportance = when (randomImportanceValue) {
            1 -> Importance.URGENT
            2 -> Importance.LOW
            else -> null
        }

        return TodoItem(
            text = notes[randomValueForText],
            deadline = Date(),
            creationDate = Date(),
            importance = randomImportance,
            isCompleted = Random().nextBoolean()
        )
    }


}