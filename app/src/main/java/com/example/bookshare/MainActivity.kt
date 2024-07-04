package com.example.bookshare

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val books = remember { mutableStateListOf(
                Book("Atomic Habits", R.drawable.atomic_habits, "John Doe", "123-456-7890", "New York", "Self-Help"),
                Book("Psychology of Money", R.drawable.psychology_of_money, "Jane Doe", "098-765-4321", "Los Angeles", "Finance"),
                Book("The Silent Patient", R.drawable.the_silent_patient, "Jim Doe", "555-555-5555", "Chicago", "Thriller"),
                Book("Rich Dad Poor Dad", R.drawable.rich_dad_poor_dad, "Jim Doe", "555-555-5555", "Chicago", "Finance"),
                Book("Tuesdays with Morrie", R.drawable.tuesdays_with_morrie, "Jim Doe", "555-555-5555", "Chicago", "Biography"),
                Book("The Immortals of Meluha", R.drawable.the_immortals_of_meluha, "Jim Doe", "555-555-5555", "Chicago", "Fantasy"),
                Book("The Secret of Nagas", R.drawable.the_secret_of_nagas, "Jim Doe", "555-555-5555", "Chicago", "Fantasy"),
                Book("The Oath of Vayuputras", R.drawable.the_oath_of_the_vayuputras, "Jim Doe", "555-555-5555", "Chicago", "Fantasy"),
                Book("The Alchemist", R.drawable.the_alchemist, "Jim Doe", "555-555-5555", "Chicago", "Fiction")
            ) }
            AppNavigation(books)
        }
    }
}
@Composable
fun BackgroundWrapper(
    color: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        content()
    }
}

@Composable
fun MainScreen(onNextClick: () -> Unit) {
    BackgroundWrapper(color = Color.LightGray) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val image: Painter = painterResource(R.drawable.bookshare_logo)
                Image(
                    painter = image,
                    contentDescription = "BookShare Logo",
                    modifier = Modifier.size(500.dp)
                )

                Text(
                    text = "BookShare",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = onNextClick) {
                    Text(text = "Next")
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    BackgroundWrapper(color = Color.Blue) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineLarge
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.padding(16.dp)
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onLogin(username, password) }) {
                    Text(text = "Submit")
                }
            }
        }
    }
}

@Composable
fun RecommendationScreen(onBackClick: () -> Unit) {
    val genres = listOf("Fiction", "Non-Fiction", "Science", "History", "Fantasy")
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var recommendedBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun fetchRecommendations(genre: String) {
        coroutineScope.launch {
            loading = true
            recommendedBooks = fetchBooksFromGithub(genre)
            loading = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Select a Genre",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }

            items(genres) { genre ->
                Column {
                    Button(
                        onClick = {
                            selectedGenre = genre
                            fetchRecommendations(genre)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(text = genre)
                    }

                    if (selectedGenre == genre) {
                        Spacer(modifier = Modifier.height(16.dp))

                        if (loading) {
                            CircularProgressIndicator()
                        } else {
                            Text(
                                text = "Recommended Books for $selectedGenre",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            recommendedBooks.forEach { book ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = book.imageResId),
                                            contentDescription = null,
                                            modifier = Modifier.size(50.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(text = book.name, style = MaterialTheme.typography.bodyLarge)
                                            Text(text = "by ${book.owner}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Back")
                }
            }
        }
    }
}


@Composable
fun ProfileScreen(receivedRequests: List<String>, onBackClick: () -> Unit) {
    val givenAwayBooks = listOf("Atomic Habits", "Psychology of Money")
    val boughtBooks = listOf("The Silent Patient", "Rich Dad Poor Dad")

    var showGivenAwayBooks by remember { mutableStateOf(false) }
    var showBoughtBooks by remember { mutableStateOf(false) }
    var showReceivedRequests by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Profile", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showGivenAwayBooks = !showGivenAwayBooks },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Books Given Away")
            }

            if (showGivenAwayBooks) {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    givenAwayBooks.forEach { book ->
                        Text(text = book, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showBoughtBooks = !showBoughtBooks },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Books Bought")
            }

            if (showBoughtBooks) {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    boughtBooks.forEach { book ->
                        Text(text = book, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showReceivedRequests = !showReceivedRequests },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Received Requests")
            }

            if (showReceivedRequests) {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    receivedRequests.forEach { request ->
                        Text(text = request, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Back")
            }
        }
    }
}

@Composable
fun BookDetailsScreen(book: Book, onBackClick: () -> Unit, onRequestClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val image: Painter = painterResource(book.imageResId)
            Image(
                painter = image,
                contentDescription = book.name,
                modifier = Modifier.size(128.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = book.name, style = MaterialTheme.typography.headlineLarge)
            Text(text = "Owner: ${book.owner}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Contact: ${book.contact}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Location: ${book.location}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onBackClick) {
                Text(text = "Back")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRequestClick) {
                Text(text = "Request Book")
            }
        }
    }
}
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(BorderStroke(2.dp, Color.Black)) // Add dark border here
            .padding(16.dp),
        singleLine = true,
        decorationBox = { innerTextField ->
            if (query.isEmpty()) {
                Text("Search books...", color = Color.Gray)
            }
            innerTextField()
        }
    )
}
@Composable
fun BookItem(book: Book, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, Color.Gray))
            .clickable { onClick() }, // Make the book item clickable
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image: Painter = painterResource(book.imageResId)
        Image(
            painter = image,
            contentDescription = book.name,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = book.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@Composable
fun BookListScreen(books: MutableList<Book>,onBookClick: (Book) -> Unit, onProfileClick: () -> Unit, onRecommendationClick: () -> Unit, onAddBookClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()



    val filteredBooks = books.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val logo: Painter = painterResource(R.drawable.bookshare_logo)
                Image(
                    painter = logo,
                    contentDescription = "BookShare Logo",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "BookShare",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Left
                )
                IconButton(onClick = onProfileClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.bookshare_logo),
                        contentDescription = "Profile Icon"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRecommendationClick) {
                Text(text = "Get Recommendations")
            }
            FloatingActionButton(
                onClick = onAddBookClick,
                modifier = Modifier
                    .padding(16.dp)

            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Book")
            }
            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { newQuery ->
                    searchQuery = newQuery
                    val index = filteredBooks.indexOfFirst { book ->
                        book.name.contains(newQuery, ignoreCase = true) ||
                                book.location.contains(newQuery, ignoreCase = true)
                    }
                    if (index != -1) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(filteredBooks) { book ->
                    BookItem(book = book, onClick = { onBookClick(book) })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


        }
    }
}



@Composable
fun AddBookScreen(
    onAddBook: (Book) -> Unit,
    onBackClick: () -> Unit
) {
    var bookName by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Book",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = bookName,
                onValueChange = { bookName = it },
                label = { Text("Book Name") },
                modifier = Modifier.padding(16.dp)
            )
            TextField(
                value = owner,
                onValueChange = { owner = it },
                label = { Text("Owner") },
                modifier = Modifier.padding(16.dp)
            )
            TextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact") },
                modifier = Modifier.padding(16.dp)
            )
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.padding(16.dp)
            )
            TextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Genre") },
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (bookName.isNotEmpty() && owner.isNotEmpty() && contact.isNotEmpty() && location.isNotEmpty() && genre.isNotEmpty()) {
                            val newBook = Book(bookName, R.drawable.bookshare_logo, owner, contact, location, genre)
                            onAddBook(newBook)
                        }
                    }
                ) {
                    Text("Add")
                }
                Button(onClick = onBackClick) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun RequestDetailsScreen(request: String, onAcceptClick: () -> Unit, onRejectClick: () -> Unit, onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Request Details", style = MaterialTheme.typography.headlineLarge)
            Text(text = request, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Button(onClick = onAcceptClick, modifier = Modifier.padding(8.dp)) {
                    Text(text = "Accept")
                }
                Button(onClick = onRejectClick, modifier = Modifier.padding(8.dp)) {
                    Text(text = "Reject")
                }
            }

            Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                Text(text = "Back")
            }
        }
    }
}

@Composable
fun PaymentScreen(onPaymentClick: () -> Unit, onBackClick: () -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Payment Details", style = MaterialTheme.typography.headlineLarge)

            TextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = { Text("Card Number") },
                modifier = Modifier.padding(8.dp)
            )
            TextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = { Text("Expiry Date") },
                modifier = Modifier.padding(8.dp)
            )
            TextField(
                value = cvv,
                onValueChange = { cvv = it },
                label = { Text("CVV") },
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onPaymentClick, modifier = Modifier.padding(8.dp)) {
                Text(text = "Pay")
            }
            Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                Text(text = "Back")
            }
        }
    }
}

@Composable
fun ShipmentScreen(onConfirmClick: () -> Unit, onBackClick: () -> Unit) {
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Shipping Details", style = MaterialTheme.typography.headlineLarge)

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.padding(8.dp)
            )
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.padding(8.dp)
            )
            TextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("Postal Code") },
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onConfirmClick, modifier = Modifier.padding(8.dp)) {
                Text(text = "Confirm")
            }
            Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                Text(text = "Back")
            }
        }
    }
}


@Composable
fun AppNavigation(books: MutableList<Book>) {
    var currentScreen by remember { mutableStateOf("Main") }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var receivedRequests by remember { mutableStateOf<List<String>>(emptyList()) }

    when (currentScreen) {
        "Main" -> MainScreen { currentScreen = "Login" }
        "Login" -> LoginScreen { username, password ->
            if (username == "g" && password == "g") {
                currentScreen = "Welcome"
            }
        }
        "Welcome" -> BookListScreen(
            books = books,
            onBookClick = { book ->
                selectedBook = book
                currentScreen = "Details"
            },
            onProfileClick = { currentScreen = "Profile" },
            onRecommendationClick = { currentScreen = "Recommendation" },
            onAddBookClick = { currentScreen = "AddBook" }
            // Add this line
        )
        "Details" -> selectedBook?.let { book ->
            BookDetailsScreen(
                book = book,
                onBackClick = { currentScreen = "Welcome" },
                onRequestClick = {
                    receivedRequests = receivedRequests + "Request for ${book.name}"
                    currentScreen = "RequestDetails"
                }
            )
        }
        "RequestDetails" -> receivedRequests.lastOrNull()?.let { request ->
            RequestDetailsScreen(
                request = request,
                onAcceptClick = { currentScreen = "Payment" },
                onRejectClick = { currentScreen = "Welcome" },
                onBackClick = { currentScreen = "Welcome" }
            )
        }
        "Payment" -> PaymentScreen(
            onPaymentClick = { currentScreen = "Shipment" },
            onBackClick = { currentScreen = "Welcome" }
        )
        "Shipment" -> ShipmentScreen(
            onConfirmClick = { currentScreen = "Welcome" },
            onBackClick = { currentScreen = "Welcome" }
        )
        "Profile" -> ProfileScreen(
            receivedRequests = receivedRequests,
            onBackClick = { currentScreen = "Welcome" }
        )
        "Recommendation" -> RecommendationScreen(
            onBackClick = { currentScreen = "Welcome" }
        )
        "RequestConfirmation" -> RequestConfirmationScreen {
            currentScreen = "Welcome"
        }
        "AddBook" -> AddBookScreen(
            onAddBook = { newBook ->
                // Handle the added book here, e.g., add it to a list of books
                books += newBook // Add the new book to the list of books
                currentScreen = "Welcome" // Set the current screen back to "Welcome"
            },
            onBackClick = { currentScreen = "Welcome" }
        )

    }
}


@Composable
fun RequestConfirmationScreen(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Your request has been sent!", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onBackClick) {
                Text(text = "Back to Home")
            }
        }
    }
}




data class Book(
    val name: String,
    val imageResId: Int,
    val owner: String,
    val contact: String,
    val location: String,
    val genre: String,
    var requestedBy: String? = null
)

suspend fun fetchBooksFromGithub(genre: String): List<Book> {
    return withContext(Dispatchers.IO) {
        try {
            val url = when (genre) {
                "Fiction" -> URL("https://raw.githubusercontent.com/Ganesh-Nagarampalli/bookrecoms/main/fiction.txt")
                "Non-Fiction" -> URL("https://raw.githubusercontent.com/Ganesh-Nagarampalli/bookrecoms/main/non-fiction.txt")
                "Science" -> URL("https://raw.githubusercontent.com/Ganesh-Nagarampalli/bookrecoms/main/science.txt")
                "History" -> URL("https://raw.githubusercontent.com/Ganesh-Nagarampalli/bookrecoms/main/history.txt")
                "Fantasy" -> URL("https://raw.githubusercontent.com/Ganesh-Nagarampalli/bookrecoms/main/fantasy.txt")
                else -> throw IllegalArgumentException("Unknown genre: $genre")
            }

            // Log the URL being accessed
            Log.d("FetchBooks", "Accessing URL: $url")

            // Read the content from the URL
            val content = url.readText()

            // Log the content fetched
            Log.d("FetchBooks", "Content fetched: $content")

            val bookLines = content.lines().filter { it.isNotBlank() }

            // Log the number of lines fetched
            Log.d("FetchBooks", "Number of lines fetched: ${bookLines.size}")

            // Parse the lines into books with only names
            val books = bookLines.map { line ->
                Book(line, R.drawable.bookshare_logo, "Unknown", "Unknown", "Unknown", genre)
            }

            // Log the books parsed
            Log.d("FetchBooks", "Books parsed: ${books.size}")

            books
        } catch (e: Exception) {
            Log.e("FetchBooks", "Error fetching books from Github", e)
            emptyList()  // Return an empty list if there's an error
        }
    }
}







