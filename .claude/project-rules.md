# Правила работы с проектом HomeTasker

## ОБЩИЕ УСТАНОВКИ

- **Язык общения**: русский (в коде - английские названия, комментарии - русские)
- **Роль**: Senior Kotlin Developer
- **Стилистика**: строго придерживаться существующего стиля кода в проекте
- **Тип приложения**: Offline-first приложение для учёта дел и расписания домашних обязанностей

---

## АРХИТЕКТУРА

### Паттерн: MVVM + Clean Architecture (3 слоя)

**Слои:**

- **Presentation Layer**: Jetpack Compose + ViewModels + UI State
- **Domain Layer**: UseCases + Domain Models + Repository Interfaces
- **Data Layer**: Room Database + DataStore + Repository Implementations

### Структура Repository

```kotlin
// Domain layer - интерфейс
interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}

// Data layer - реализация
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val mapper: TaskMapper
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insert(mapper.toEntity(task))
    }
}
```

### Структура UseCase

```kotlin
class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(
        filter: TaskFilter = TaskFilter.ALL,
        sortBy: SortOption = SortOption.DATE
    ): Flow<List<Task>> {
        return repository.getTasks()
            .map { tasks ->
                tasks.filter { applyFilter(it, filter) }
                     .sortedWith(getSorter(sortBy))
            }
    }
}
```

### Структура ViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {
    // 1. Private mutable state
    private val _uiState = MutableStateFlow(HomeUiState())

    // 2. Public immutable state
    val uiState = _uiState.asStateFlow()

    // 3. Init block
    init {
        loadTasks()
    }

    // 4. Private functions
    private fun loadTasks() {
        viewModelScope.launch {
            getTasksUseCase(
                filter = _uiState.value.filter,
                sortBy = _uiState.value.sortBy
            ).collect { tasks ->
                _uiState.update { it.copy(
                    tasks = tasks,
                    isLoading = false
                )}
            }
        }
    }

    // 5. Public functions (actions)
    fun onTaskCheckedChange(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            toggleTaskCompletionUseCase(task, isCompleted)
        }
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }
}
```

### Структура UI State

```kotlin
data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val filter: TaskFilter = TaskFilter.ALL,
    val sortBy: SortOption = SortOption.DATE,
    val searchQuery: String = ""
)
```

### Структура Composable Screen

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTaskDetail: (Long) -> Unit,
    onNavigateToCreateTask: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Задачи") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateTask) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.tasks.isEmpty() -> {
                EmptyState(
                    message = "Нет задач",
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                TaskList(
                    tasks = uiState.tasks,
                    onTaskClick = { onNavigateToTaskDetail(it.id) },
                    onTaskCheckedChange = viewModel::onTaskCheckedChange,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}
```

---

## NAMING CONVENTIONS

### Классы и типы

- **Entity (Room)**: `TaskEntity`, `CategoryEntity`, `TimeTrackingSessionEntity`
- **Domain Model**: `Task`, `Category`, `TimeTrackingSession`
- **DAO**: `TaskDao`, `CategoryDao`, `TimeTrackingDao`
- **Repository Interface**: `TaskRepository`, `CategoryRepository`
- **Repository Impl**: `TaskRepositoryImpl`, `CategoryRepositoryImpl`
- **UseCase**: `GetTasksUseCase`, `CreateTaskUseCase`, `ToggleTaskCompletionUseCase`
- **ViewModel**: `HomeViewModel`, `TaskDetailViewModel`, `CalendarViewModel`
- **Screen**: `HomeScreen`, `TaskDetailScreen`, `CalendarScreen`
- **UI State**: `HomeUiState`, `TaskDetailUiState`, `CalendarUiState`
- **Mapper**: `TaskMapper`, `CategoryMapper`

### Переменные

- **Private mutable**: `_uiState`, `_isLoading`, `_tasks`
- **Public immutable**: `uiState`, `isLoading`, `tasks`
- **Константы**: `DATABASE_NAME`, `NOTIFICATION_CHANNEL_ID` (UPPER_SNAKE_CASE)

### Функции

- **camelCase**: `loadTasks()`, `filterByCategory()`, `startTracking()`
- **Composable**: PascalCase -> `HomeScreen()`, `TaskCard()`, `TimerDisplay()`
- **UseCase invoke**: `operator fun invoke(...)`

---

## СТИЛИСТИКА КОДА

### 1. State Updates

```kotlin
// ПРАВИЛЬНО
_uiState.update { it.copy(isLoading = true) }

// НЕПРАВИЛЬНО
_uiState.value = _uiState.value.copy(isLoading = true)
```

### 2. Строки (всегда hardcoded на русском)

```kotlin
// ПРАВИЛЬНО
Text("Задачи")
Text("Добавить задачу")
Text("Выполнено: ${task.completedCount}")

// НЕПРАВИЛЬНО
Text(stringResource(R.string.tasks))
```

### 3. Flow в Repository

```kotlin
// ПРАВИЛЬНО - Flow для наблюдаемых данных
fun getTasks(): Flow<List<Task>>

// ПРАВИЛЬНО - suspend для одиночных операций
suspend fun getTaskById(id: Long): Task?
suspend fun insertTask(task: Task): Long
```

### 4. Dependency Injection (Hilt)

```kotlin
// ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel()

// UseCase
class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
)

// Repository
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val mapper: TaskMapper
) : TaskRepository

// В Composable
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
)
```

### 5. Room Entity

```kotlin
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String? = null,

    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,

    val priority: Priority = Priority.NONE,
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "due_date")
    val dueDate: LocalDate? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### 6. Room DAO

```kotlin
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY due_date ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}
```

---

## ТЕХНОЛОГИЧЕСКИЙ СТЕК

### Обязательные библиотеки

- **UI**: Jetpack Compose + Material3 (Material You)
- **DI**: Hilt 2.52+
- **Navigation**: Jetpack Navigation Compose 2.8.x
- **Database**: Room 2.6.x
- **Preferences**: DataStore Preferences 1.1.x
- **Async**: Kotlin Coroutines + Flow + StateFlow
- **Charts**: Vico (для статистики)
- **Animations**: Lottie Compose 6.x (для empty states)
- **Widget**: Glance 1.1.x (Compose for widgets)
- **Serialization**: kotlinx-serialization 1.7.x (для экспорта/импорта JSON)
- **Date/Time**: Core Library Desugaring 2.1.x (java.time для API < 26)

### Build Configuration

- **Kotlin**: 2.0.x
- **Compile SDK**: 36
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Java**: 17
- **Compose BOM**: 2024.12.01

---

## СТРУКТУРА ПРОЕКТА

```text
com.example.hometasks/
├── di/                          # Hilt modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── data/                        # Data Layer
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── TaskDao.kt
│   │   │   │   ├── CategoryDao.kt
│   │   │   │   └── TimeTrackingDao.kt
│   │   │   ├── entity/
│   │   │   │   ├── TaskEntity.kt
│   │   │   │   ├── CategoryEntity.kt
│   │   │   │   ├── TaskInstanceEntity.kt
│   │   │   │   └── TimeTrackingSessionEntity.kt
│   │   │   └── converter/
│   │   │       └── Converters.kt
│   │   └── datastore/
│   │       └── SettingsDataStore.kt
│   ├── repository/
│   │   ├── TaskRepositoryImpl.kt
│   │   ├── CategoryRepositoryImpl.kt
│   │   ├── TimeTrackingRepositoryImpl.kt
│   │   └── SettingsRepositoryImpl.kt
│   └── mapper/
│       ├── TaskMapper.kt
│       └── CategoryMapper.kt
│
├── domain/                      # Domain Layer
│   ├── model/
│   │   ├── Task.kt
│   │   ├── Category.kt
│   │   ├── TimeTrackingSession.kt
│   │   ├── TaskInstance.kt
│   │   ├── Priority.kt
│   │   ├── RepeatType.kt
│   │   └── TrackingStatus.kt
│   ├── repository/
│   │   ├── TaskRepository.kt
│   │   ├── CategoryRepository.kt
│   │   ├── TimeTrackingRepository.kt
│   │   └── SettingsRepository.kt
│   └── usecase/
│       ├── task/
│       │   ├── GetTasksUseCase.kt
│       │   ├── GetTaskByIdUseCase.kt
│       │   ├── CreateTaskUseCase.kt
│       │   ├── UpdateTaskUseCase.kt
│       │   ├── DeleteTaskUseCase.kt
│       │   ├── ToggleTaskCompletionUseCase.kt
│       │   └── SearchTasksUseCase.kt
│       ├── category/
│       │   ├── GetCategoriesUseCase.kt
│       │   ├── CreateCategoryUseCase.kt
│       │   └── DeleteCategoryUseCase.kt
│       ├── tracking/
│       │   ├── StartTrackingUseCase.kt
│       │   ├── PauseTrackingUseCase.kt
│       │   ├── ResumeTrackingUseCase.kt
│       │   └── StopTrackingUseCase.kt
│       ├── statistics/
│       │   └── GetStatisticsUseCase.kt
│       └── export/
│           ├── ExportDataUseCase.kt
│           └── ImportDataUseCase.kt
│
├── presentation/                # Presentation Layer
│   ├── MainActivity.kt
│   ├── navigation/
│   │   ├── NavGraph.kt
│   │   └── Screen.kt
│   ├── theme/
│   │   ├── Theme.kt
│   │   ├── Color.kt
│   │   └── Type.kt
│   ├── components/              # Reusable UI components
│   │   ├── TaskCard.kt
│   │   ├── CategoryChip.kt
│   │   ├── PriorityIndicator.kt
│   │   ├── TimerDisplay.kt
│   │   ├── EmptyState.kt
│   │   ├── SearchBar.kt
│   │   └── FilterChips.kt
│   └── screens/
│       ├── home/
│       │   ├── HomeScreen.kt
│       │   ├── HomeViewModel.kt
│       │   └── HomeUiState.kt
│       ├── calendar/
│       │   ├── CalendarScreen.kt
│       │   ├── CalendarViewModel.kt
│       │   └── CalendarUiState.kt
│       ├── task_detail/
│       │   ├── TaskDetailScreen.kt
│       │   ├── TaskDetailViewModel.kt
│       │   └── TaskDetailUiState.kt
│       ├── task_edit/
│       │   ├── TaskEditScreen.kt
│       │   ├── TaskEditViewModel.kt
│       │   └── TaskEditUiState.kt
│       ├── categories/
│       │   ├── CategoriesScreen.kt
│       │   ├── CategoriesViewModel.kt
│       │   └── CategoriesUiState.kt
│       ├── statistics/
│       │   ├── StatisticsScreen.kt
│       │   ├── StatisticsViewModel.kt
│       │   └── StatisticsUiState.kt
│       ├── settings/
│       │   ├── SettingsScreen.kt
│       │   ├── SettingsViewModel.kt
│       │   └── SettingsUiState.kt
│       ├── search/
│       │   ├── SearchScreen.kt
│       │   └── SearchViewModel.kt
│       └── onboarding/
│           ├── OnboardingScreen.kt
│           └── OnboardingViewModel.kt
│
├── service/                     # Background services
│   ├── TimerService.kt          # Foreground service для таймера
│   └── NotificationService.kt
│
├── receiver/                    # Broadcast receivers
│   ├── AlarmReceiver.kt         # Напоминания
│   └── BootReceiver.kt          # Перепланирование после перезагрузки
│
├── widget/                      # App widget (Glance)
│   ├── TaskWidgetProvider.kt
│   ├── TaskWidgetReceiver.kt
│   └── TaskWidgetContent.kt
│
└── util/                        # Utilities
    ├── DateTimeUtils.kt
    ├── NotificationUtils.kt
    └── JsonUtils.kt
```

### При создании новой фичи

```text
presentation/screens/<feature>/
├── <Feature>Screen.kt           # Composable UI
├── <Feature>ViewModel.kt        # State management
└── <Feature>UiState.kt          # UI State data class
```

---

## МОДЕЛЬ ДАННЫХ

### Priority (Приоритет)

```kotlin
enum class Priority {
    NONE,   // Без приоритета (серый)
    LOW,    // Низкий (синий)
    MEDIUM, // Средний (жёлтый/оранжевый)
    HIGH    // Высокий (красный)
}
```

### RepeatType (Тип повторения)

```kotlin
enum class RepeatType {
    NONE,           // Без повторения
    DAILY,          // Каждый день
    WEEKLY,         // Каждую неделю
    MONTHLY,        // Каждый месяц
    CUSTOM_DAYS,    // По дням недели (пн, ср, пт)
    EVERY_N_DAYS,   // Каждые N дней
    BIWEEKLY_ODD,   // По нечётным неделям
    BIWEEKLY_EVEN   // По чётным неделям
}
```

### TrackingStatus (Статус трекинга)

```kotlin
enum class TrackingStatus {
    IN_PROGRESS,
    PAUSED,
    COMPLETED
}
```

### Предустановленные категории

| Категория      | Подкатегории                          |
|----------------|---------------------------------------|
| Учёба          | ВУЗ, Курсы, Стажировка, Самообразование |
| Уборка         | Ежедневная, Генеральная, Стирка       |
| Покупки        | Продукты, Бытовое, Одежда             |
| Готовка        | Завтрак, Обед, Ужин, Заготовки        |
| Здоровье       | Спорт, Медицина, Привычки             |
| Дом            | Ремонт, Растения, Питомцы             |
| Прочее         | —                                     |

---

## ГРАФИКИ И СТАТИСТИКА (Vico)

### Доступные компоненты

```kotlin
// График выполнения задач по дням
@Composable
fun TaskCompletionLineChart(
    data: List<Pair<LocalDate, Int>>, // Дата, количество выполненных
    modifier: Modifier = Modifier
)

// Распределение по категориям (круговая диаграмма)
@Composable
fun CategoryDistributionChart(
    data: List<Pair<String, Int>>, // Название категории, количество задач
    modifier: Modifier = Modifier
)

// Продуктивность по дням недели
@Composable
fun WeekdayProductivityChart(
    data: List<Pair<DayOfWeek, Int>>, // День недели, количество
    modifier: Modifier = Modifier
)

// Сравнение оценочного и фактического времени
@Composable
fun TimeEstimationAccuracyChart(
    data: List<Triple<String, Int, Int>>, // Задача, оценка, факт (минуты)
    modifier: Modifier = Modifier
)
```

---

## УВЕДОМЛЕНИЯ

### Notification Channels

```kotlin
object NotificationChannels {
    const val REMINDERS = "task_reminders"      // Напоминания о задачах
    const val TIMER = "timer_tracking"          // Уведомление таймера
    const val OVERDUE = "overdue_tasks"         // Просроченные задачи
}
```

### Работа с AlarmManager

```kotlin
// Планирование напоминания
fun scheduleReminder(context: Context, task: Task, reminderTime: LocalDateTime) {
    val alarmManager = context.getSystemService<AlarmManager>()
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(EXTRA_TASK_ID, task.id)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        task.id.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager?.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        pendingIntent
    )
}
```

---

## АНТИ-ПАТТЕРНЫ (НЕ ДЕЛАТЬ)

### НЕПРАВИЛЬНО

```kotlin
// 1. Не использовать strings.xml
Text(stringResource(R.string.title))

// 2. Не мутировать state напрямую
_uiState.value = _uiState.value.copy(isLoading = true)

// 3. Не использовать GlobalScope
GlobalScope.launch { }

// 4. Не игнорировать ошибки
try {
    dao.insert(task)
} catch (e: Exception) {
    // пусто
}

// 5. Не создавать ViewModel вручную
val viewModel = HomeViewModel(useCase)

// 6. Не использовать lateinit для Compose state
lateinit var tasks: List<Task>

// 7. Не блокировать UI thread
runBlocking {
    repository.getData()
}

// 8. Не использовать LiveData
private val _tasks = MutableLiveData<List<Task>>()
```

### ПРАВИЛЬНО

```kotlin
// 1. Hardcoded русские строки
Text("Задачи")

// 2. Immutable updates через update
_uiState.update { it.copy(isLoading = true) }

// 3. viewModelScope
viewModelScope.launch { }

// 4. Логирование ошибок
try {
    dao.insert(task)
} catch (e: Exception) {
    Log.e(TAG, "Failed to insert task", e)
    _uiState.update { it.copy(error = "Не удалось сохранить задачу") }
}

// 5. Hilt injection
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel())

// 6. Compose state
val tasks by viewModel.tasks.collectAsState()

// 7. Корутины для async операций
viewModelScope.launch {
    repository.getTasks()
}

// 8. StateFlow вместо LiveData
private val _tasks = MutableStateFlow<List<Task>>(emptyList())
val tasks = _tasks.asStateFlow()
```

---

## ПРИНЦИПЫ РАЗРАБОТКИ

1. **DRY** (Don't Repeat Yourself) - переиспользуемые компоненты в `presentation/components/`
2. **SOLID** - особенно Single Responsibility (UseCase на каждую операцию) и Dependency Inversion (Repository interfaces в domain)
3. **Clean Architecture** - чёткое разделение слоёв, зависимости направлены внутрь
4. **Reactive State** - всегда StateFlow, никогда LiveData
5. **Offline-first** - все данные в Room, работа без интернета
6. **Type Safety** - sealed classes для навигации и результатов
7. **Immutability** - data classes для State, copy() для updates
8. **Single Activity** - одна Activity + Jetpack Navigation Compose
9. **Unidirectional Data Flow** - события идут вверх, состояние - вниз

---

## DEBUGGING

### Логирование

```kotlin
// Простое логирование
Log.d(TAG, "Loading tasks...")

// Логирование Flow
init {
    viewModelScope.launch {
        uiState.collect { state ->
            Log.d(TAG, "State: $state")
        }
    }
}
```

### Compose Layout Inspector

- Android Studio -> Tools -> Layout Inspector
- Для debug Compose UI иерархии

### Room Database Inspector

- Android Studio -> App Inspection -> Database Inspector
- Для просмотра данных в Room

---

## ДОПОЛНИТЕЛЬНО

- **Версии библиотек**: см. `gradle/libs.versions.toml`
- **Git**: коммиты на английском, описание изменений кратко
- **Code Review**: перед PR проверить архитектуру и стиль кода

---

**Последнее обновление**: 2025-12-17
