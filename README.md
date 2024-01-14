GH KANBAN 
DOCUMENTACIÓN
Iniciamos un Empty Project.
Primero de todos debemos configurar nuestro manifest y el gradle para que nuestra aplicación funcione.
En el gradle añadimos las dependencias para Glide (librería para cargar imagenes) y Gson para recibir datos JSON y guardar localmente.

Layouts.xml:
Ya que para listar los repositorios necesitaremos un recyclerView, para cada página, he creado la vista de esa actividad y otra con el Item que se mostrará en esta lista.

La página principal está formada por un ToolBar con el título del proyecto. Debajo tenemos dos botones, Explore y Local donde posteriormente los utilizaremos para cambiar entre actividades.
Y por último el recyclerView.
Para la vista de su Item, crearemos dos TextViews, uno para el nombre y otro para el autor. A la derecha ponemos un Imagview con el símbolo + (Layout -> new -> Vector Asset)
Para la vista Local será exactamente lo mismo pero con un icono de una papelera para eliminar el repositorio.
Para las vistas de Backlog, Next, Doing y Done. Se hace igual que las anteriores, pero ponemos los textView de Issue: (Nombre del error), Date:, Comments: , IssueNumber: , en el toolbar cambiamos el título y el número de página. En el Item lo que varia es que en Backlog el imageView tiene solo este icono “->”, en Next “->” “<-”,  Doing “->” “<-”,  Done “<-”.
Con estas flechas podremos mover un Issue a la siguiente vista o eliminarlo.

Actividades:
Repository
Creamos una clase Repository donde inicializamos las variables name y author. Creamos constructores, getters y setters.
Adaptery:
Creamos la clase Adaptery que será la responsable de proporcionar los datos y crear las vistas para cada elemento en el RecyclerView.


Atributos:
Context:En este caso, mContext se utiliza para cargar imágenes con Glide, iniciar una actividad al hacer clic en un elemento, y otras operaciones relacionadas con la interfaz de usuario. 
rData: La variable mData es una lista que contiene objetos de la clase Repository. Esta lista representa los datos que se mostrarán en el RecyclerView.
El método onCreateViewHolder se utiliza para representar y mantener las vistas de cada elemento en el RecyclerView,
onBindViewHolder es un método que se llama cuando se necesita mostrar datos en una vista específica.
Cuando el RecyclerView necesita mostrar un nuevo elemento (o actualizar uno existente debido a desplazamiento), invoca este método y pasa la posición del elemento que debe mostrarse y el holder que contiene las vistas ya infladas. Esto permite que RecyclerView recicle y reutilice las vistas.
También contiene un Intent para coger los datos del repositorio y pasarlo a la actividad Local.
MyViewHolder se utiliza para representar y mantener las vistas de cada elemento en el RecyclerView,

MainActivity:
Atributos:
   - RecyclerView repoRecyclerView: Representa la lista de repositorios.
   - Adaptery adaptery: Adaptador personalizado para mostrar la lista de repositorios.
   - List<Repository> fullRepoList: Almacena la lista completa de repositorios obtenida de la API de GitHub.
   - List<Repository> displayedRepoList: Contiene la lista de repositorios que se mostrarán en el RecyclerView.
   - List<Repository> receivedData: Almacena temporalmente los datos obtenidos de la API.

Constantes:
   - PREFERENCES_NAME: Nombre para las preferencias compartidas utilizado para almacenar datos localmente.
   - KEY_DATA: Clave utilizada para almacenar y recuperar datos en las preferencias compartidas.
   - BASE_URL: URL base para la API de GitHub.

Métodos principales:
   - onCreate(Bundle savedInstanceState): Se llama cuando la actividad se crea. Inicializa la interfaz de usuario, carga datos y configura listeners.
   - `loadData()`: Carga los datos de la API de GitHub utilizando una tarea asíncrona, previamente comprobamos si ya tenemos datos guardados para no volver ha hacer una llamada a la api. Aquí creamos un intent para que cuando pulsemos el botón de local vayamos a la actividad Local.


   - loadStoredData(): Recupera datos almacenados localmente y los carga en la lista de repositorios.
   - parseStoredData(String storedData): Convierte una cadena JSON almacenada en preferencias compartidas en una lista de objetos Repository.
   - `saveDataLocally(List<Repository> repositories): Guarda la lista de repositorios en preferencias compartidas en formato JSON.
   - putDataIntoRecyclerView(List<Repository> repositories): Configura el adaptador y muestra la lista de repositorios en el RecyclerView.
   - isDataLoaded(): Verifica si los datos ya se han cargado previamente.

-GetData`:
   - Es una subclase AsyncTask que realiza la obtención de datos de la API de GitHub en segundo plano.
   - `doInBackground(Void... voids)`: Realiza la solicitud HTTP a la API y procesa los datos JSON.
   - `onPostExecute(List<Repository> repositories)`: Actualiza la interfaz de usuario con los datos obtenidos y los almacena localmente.

Persistencia de estado:
   - `onSaveInstanceState(Bundle outState)`: Guarda el estado actual de la aplicación, en este caso, la URL base.

LocalAdptery:
Obtenemos el nombre y autor del MainActivty con un intent.
Lo mismo que Adaptery pero creando una función para eliminar el repositorio al hacer click al imageView creamos nuevas funciones para guardar, actualizar y volver a cargar los datos guardados.
También tenemos un método click para que cuando pulsemos el nombre del repositorio se cree un intent para utilizar este nombre en la otra API de errores.



LocalActivity:
Casi misma funcionalidad que las clase Main pero no hacemos llamada a la API, y creamos un nuevo método y evento:


addRepositoryToLocalRecyclerView(Repository repository): Agrega un nuevo repositorio a la lista local, actualiza el adaptador y guarda los cambios localmente.

exploreButton.setOnClickListener(...): Maneja el evento del botón "Explore" y vuelve a la actividad anterior (a través de onBackPressed()).

localAdapter.setOnItemClickListener(...): Establece un listener para los clics en los elementos del RecyclerView y abre BacklogActivity con el nombre y autor del repositorio seleccionado.

Issue:
Nueva clase Donde donde inicializamos nuevos atributos donde pondremos los datos que recibimos de la api de issues de GitHub.
BacklogAdapter:
Este adapter es lo mismo que los anteriores y crea un intent para luego obtener estos datos y cargarlos a la actividad de Next.
BacklogActivity:
En esta nueva clase es prácticamente idéntica al MainActivity lo único que obtiene  el nombre y autor de la clase LocalActivity mediante un intent y los utilizamos para  cargar la API de issues.

NextAdapter:
Misma dinámica que LocalAdapter. 

NextActivity:
En esta actividad cargamos los datos que recibimos del Intent. Cargamos y guardamos los datos localmente.

El restos de actividades y adaptadores son iguales que el adaptador y actividad de Next.

(Doing Adapter, DoingActivity, DoneAdapter, DoneActivity)

En donde solo se puede eliminar repositorios.


Reflexión Personal:
En este proyecto se puede mejorar mucho más ya que por ejemplo utilizamos HTTP URL connection que está un poco desactualizado y se podría cambiar por retrofit que es más nuevo y está más optimizado. 
 Luego cuando llamamos la API al OnCreate no es la manera correcta de hacerlo ya que es peligroso y se debería de poner en otro método se llama OnStart que es más seguro.
Un error que he detectado en mi aplicación y no he sabido cómo arreglarlo es que cuando pulsamos en el repositorio para agregar para pasarlo a otra actividad no se pasa el momento y debemos de ir atrás y volver a ir hacia adelante para que se cargue, además de esto, se crean repositorios vacíos de más. Para acabar me gustaría haber implementado un spinner, que finalmente no he podido porque me fallaba la aplicación, así sería más fácil navegar entre actividades.

