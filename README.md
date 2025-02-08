# Java 8 Cheatsheet
# General
## Basics
### String
```
String(byte[])
getBytes()
charAt(index)
concat(other)
equals(other)
compareTo(other)
indexOf(char)
subString(begin), subString(begin, end)
toLowerCase(), toUpperCase()
split(regex)
matches(regex)
replace(search, replace)
trim()
System.out.println(string);
String.valueOf(primitve) 
Integer.parseInt(string);

Scanner sc = new Scanner(System.in);
sc.nextInt()
```
### StringBuilder
```
length()
charAt(index)
setCharAt(index, char)
reverse()
append(other)
insert(index, str)
delete(begin, end)
deleteCharAt(index)
```
### Random and Math
```
(new Random()).nextInt(bound)
Math.random() // 0 <= r <= 1
Math.pow()
Math.abs()
Math.PI
```

### Enums
```
enum E1 {V1, V2, V3};
E e = E.V1; 

enum E2 {
    V1(name);
    private String name;
    Suit(String name){ this.name = name;}
    public String getName(){return name;}
} 
E2[] values = E.values();
for (int i=0; i < values.length; i++)
    System.out.println(values[i].getName()); 
```

### Variable Arguments
```
public void method1(String st, int … quantity)
```

### Commandline Arguments
```
args[0], args[1]... - commandline arguments 
```

## Classes
- public class - main class of file, only one per file.
- When getting array in ctor - consider cloning it

### Common Functions
```
public String toString() {}

public boolean equals(Object other) {
    if (other == null || !(other instanceof MyClass)) {
        return false;
    }
    MyClass otherObj = (MyClass) other;
    return otherObj.name.equals(name) && otherObj.age == age && otherObj.color.equals(color);
}

public int hashCode() 
protected void finalize() throws Throwable
public final Class<?> getClass() 
```
### Clone
```
public class MyClass implements Cloneable

protected Object clone() throws CloneNotSupportedException 
```
- make sure to clone members too

examples:
```
ArrayList<String> clone = new ArrayList<String>();
clone.addAll(members);

Event[] clone = arraylist.toArray(new Event[0]);

MyClass cloned = (MyClass) super.clone(); // shallow copy
cloned.member1 = member1.clone();
return cloned;

```

### Syntax
```
[modifiers] class ClassName [extends SuperClassName] [implements interfaces...] {}
[private/protected/public] [static] [final] [transient] [volatile] type variableName;
[accessSpecifier] [static] [abstract] [final] [native] [synchronized] returnType methodName(params...) [throws exceptions...] {}
```
- Abstract functions and classes - using `abstract` keyword.

### Interface
```
public interface Collection {
 void f1(Object obj);
 void f2(Object obj);
} 
```

### Comparable Interface
- -1 smaller, 0 equals, 1 greater (than other)
```
public interface Comparable {
    public int compareTo(Object o);
}

examplel:
public int compareTo(Object other) { 
    return this.idNumber.compareTo(((Person) other).idNumber);
}
```
### More
- hiding / overloading - TBD
- super - TBD
- polymorphism - TBD
  - Static type - the compile-time declared type. Dynamic type - the actual type of the object.
  - When doing `B b = (B)a` either A,B can inherit one another. 
- static/regular inner classes - TBD
  - To make them accessible from outside - class must be "public".

## Exceptions
```
public func() throws IOException {}

class E1 extends RuntimeException {
    public E1() {
        super();
    }
    public E1(String message) {
        super(message);
    }
}

throw new E1(string);

assert condition;
assert condition: message;
```
- checked exceptions - must catch or specify

## Files
example application:
```
import java.io.*;

public class Main {
    /*
     * Interfaces and Main Methods:
     * bytes:
     *  - InputStream: int read(), int read(byte[]), void close()
     *  - OutputStream: void write(int), void write(byte[]), void close()
     * chars (int):
     *  - Reader: int read(), int read(char[]), void close()
     *  - Writer: void write(int), void write(char[]), void close()
     * objects:
     * - ObjectInput: Object readObject(), int read(), void close()
     * - ObjectOutput: void writeObject(Object), void write(int), void close()
     */

    public static void main(String[] args) throws Exception {
        fileStreamExample();
        byteArrayStreamExample();
        pipedStreamExample();
        filterStreamExample();
        objectStreamExample();
        readerWriterExample();
    }

    private static void fileStreamExample() throws IOException {
        String data = "Hello, File Streams!";
        File file = new File("example.txt");
        FileOutputStream fos = null;
        FileInputStream fis = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(data.getBytes());
        } finally {
            if (fos != null) fos.close();
        }

        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            System.out.println("File content: " + new String(buffer));
        } finally {
            if (fis != null) fis.close();
        }

        file.delete();
    }

    private static void byteArrayStreamExample() throws IOException {
        String data = "Hello, ByteArray Streams!";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = null;

        try {
            baos.write(data.getBytes());
            byte[] byteArray = baos.toByteArray();
            bais = new ByteArrayInputStream(byteArray);
            byte[] buffer = new byte[bais.available()];
            bais.read(buffer);
            System.out.println("ByteArray content: " + new String(buffer));
        } finally {
            if (bais != null) bais.close();
            baos.close();
        }
    }

    private static void pipedStreamExample() throws IOException {
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);

        Thread writer = new Thread(() -> {
            try {
                pos.write("Hello, Piped Streams!".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    pos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread reader = new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int length = pis.read(buffer);
                System.out.println("Piped content: " + new String(buffer, 0, length));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    pis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        writer.start();
        reader.start();
        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void filterStreamExample() throws IOException {
        String data = "Hello, Filter Streams!";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MyFilterOutputStream fos = null;
        MyFilterInputStream fis = null;

        try {
            fos = new MyFilterOutputStream(baos);
            fos.write(data.getBytes());
        } finally {
            if (fos != null) fos.close();
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        try {
            fis = new MyFilterInputStream(bais);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            System.out.println("Filtered content: " + new String(buffer));
        } finally {
            if (fis != null) fis.close();
            bais.close();
        }
    }

    private static void objectStreamExample() throws IOException, ClassNotFoundException {
        Person person = new Person("Alice", 30);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        ByteArrayInputStream bais = null;

        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(person);
        } finally {
            if (oos != null) oos.close();
        }

        bais = new ByteArrayInputStream(baos.toByteArray());

        try {
            ois = new ObjectInputStream(bais);
            Person deserializedPerson = (Person) ois.readObject();
            System.out.println("Deserialized Person: " + deserializedPerson);
        } finally {
            if (ois != null) ois.close();
            if (bais != null) bais.close();
        }
    }

    private static void readerWriterExample() throws IOException {
        System.out.println("Enter text (press Enter to see output):");
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            writer = new BufferedWriter(new OutputStreamWriter(System.out));

            String input = reader.readLine();
            writer.newLine();
            writer.write("You entered: " + input);
            writer.flush();
        } finally {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        }
    }

    static class MyFilterInputStream extends FilterInputStream {
        protected MyFilterInputStream(InputStream in) {
            super(in);
        }

        // Implementing only one does not work because they both use input stream.
        @Override
        public int read() throws IOException {
            return Character.toUpperCase(super.read());
        }

        public int read(byte[] data, int offset, int length) throws IOException {
            int result = super.read(data, offset, length);
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) Character.toUpperCase(data[i]);
            }
            return result;
        }
    }

    static class MyFilterOutputStream extends FilterOutputStream {
        protected MyFilterOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            super.write(b);
            super.write(' ');
        }
    }


    static class Person implements Serializable {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
}
```

more examples:
```
// throw IOException
FileReader in = new FileReader("f.txt");
int c = in.read();

BufferedReader in = new BufferedReader(new FileReader("f.txt"));
String st = in.readLine();

Paths.get("c:\temp\g.txt");
DirectoryStream<Path> stream = Files.newDirectoryStream(path); 
for (Path path : directoryStream)
 System.out.println(path);
 
Formatter output = new Formatter("f.txt"); 
output.format("format", object); 
output.close(); 

Scanner input = new Scanner(Paths.get("f.txt"));  // can also get File, InputStream
while (input.hasNext()){
    String string = input.next();
    int number = input.nextInt();
}
input.close(); 
```
### PrintStream
```
PrintStream ps = new PrintStream(System.out);
ps.println("Hello, PrintStream!");
ps.printf("Formatted: %d%n", 100);
ps.close();
```
### DataStream
write/read primitive types to binary form
```
DataOutputStream dos = new DataOutputStream(new FileOutputStream("data.bin"));
dos.writeInt(42);
dos.writeDouble(3.14);
dos.writeBoolean(true);
dos.close();
DataInputStream dis = new DataInputStream(new FileInputStream("data.bin"))
int intValue = dis.readInt();
double doubleValue = dis.readDouble();
```
### LineNumberInputStream 
keeps track of line number
```
LineNumberInputStream lnStream = new LineNumberInputStream(fis);
int data;
while ((data = lnStream.read()) != -1) {
    System.out.println("Line number: " + lnStream.getLineNumber() + " - " + (char) data);
}
```

### PushbackInputStream
has unread() method
```
PushbackInputStream pbis = new PushbackInputStream(bais);
int ch = pbis.read();
pbis.unread(ch); 
System.out.println((char) pbis.read());
```

### I/O Class Hierarchy
TBD

## Containers
```
String[] array = {"a","b"};
String[] array = new String[10];
int x[][] = {{0},{1, 1},{2, 2}};
array.length

Random rnd = new Random();
int[][] data = new int[10][10];   // [row][col]
for (int i = 0; i < data.length; i++)  {
    for (int j = 0; j < data[i].length; j++) {
        data[i][j] = rnd.nextInt(999) + 1;
    }
}

new ArrayList<String>();
new ArrayList<String>(10);
ArrayList<Integer> intArray = new ArrayList<Integer>(); 

for (MyClass item : items) {}
```

### class ArrayList, class Vector:
```
add(item)
get()
set(index, iteem)
indexOf(item)
contains(item)
remove(Object item)
remove(int index
size()
toArray()
iterator()
```

### interface Iterator:
```
hasNext()
next()
remove()
```
### class Stack (Vector):
```
push(item)
pop()
peek()
empty()
```

### Conversions
```
// Array -> ArrayList 
ArrayList<String> arraylist = new ArrayList<String>(Arrays.asList(array));
ArrayList<String> arraylist = new ArrayList<String>(Arrays.asList(new String[]{"a","b"}));
// ArrayList -> Array
String[] b = arraylist.toArray(new String[0]);
``` 

### class LinkedList (List)
```
LinkedList<MyClass> queue = new LinkedList<MyClass>(); 
addFirst()
removeLast()

addLast()
getFirst()
getLast()
removeFirst()
```

### interface Queue:
```
offer()
peek()/element()
poll()/remove()

class LinkedList (Queue)
class PriorityQueue (Queue)
Comparator: compare(T one, T two) // -1, 0, 1
```

### interface Set:
``` 
interface Set:
add(item)
addAll(list)
contains(item)
containsAll(list)

class HashSet (Set):  
HashSet(list) 

interface SortedSet (Set)
```

### More
```
Collections.shuffle(arraylist);
```

## HashMap
```
interface Map: 
put(key, value)
get(key)
remove(key)
containsKey(key)
containsValue(value)

for (Map.Entry<String, String> entry : clients.entrySet()) {}
    entry.getKey();
    entry.getValue();
}

class HashMap (Map)
```
## Streams
- data source, internal operations, terminal operations
### Intermediate operations:
```
filter(predicate)
distinct()
limit(n)
map(operation)
sorted()
```

### Terminal operations:
```
forEach()
average()
count()
sum()
max()
min()
reduce()
forEach(System.out::println);
```

### Examples
```
// collect
IntStream.rangeClosed(1, 10).mapToObj(String::valueOf).collect(Collectors.joining(" "))

// mapToObj
IntStream.rangeClosed(1, 5).mapToObj(String::valueOf).forEach(System.out::println);

// Will create exception if empty without orElse
IntStream.of(array).getAsInt().orElse(0)

// reduce
IntStream.of(nums).reduce(0, (a,b) -> a*b)

// Examples:
IntStream.rangedClosed(begin, end).sum()

.map((int x) -> {return x+1;})
.map((x, y) -> (x+1))
.map(x -> x+1)

Integer[] values = {1,2,3};
Stream<Integer> stInteger = Arrays.stream(values);

List <Integer> list = Arrays.stream(values).filter(value -> value < 2).collect(Collectors.toList()); 
 
int[] numbers = IntStream.generate(() -> rand.nextInt(bound)).limit(n).toArray();

ArrayList<String> arraylist2 = (ArrayList<String>) Arrays
    .stream(arraylist.toArray(new MyClass[0]))
    .map(item -> item + "!")
    .collect(Collectors.toCollection(ArrayList::new)));

MyClass[] list = Arrays
        .stream(arraylist.toArray(new MyClass[0]))
        .map(item -> item + "!")
        .toArray(MyClass[]::new)

"[" + Arrays.stream(items.toArray()).map(String::valueOf).collect(Collectors.joining(", ")) + "]";

IntStream.range(begin, end).mapToObj(String::valueOf).toArray(String[]::new)
```

### Typer Wrapper
```
Integer num1 = 1;
int num2 = new Integer(2);
```

# GUI
## Swing
```
JOptionPane.showMessageDialog(null, message, heading, JOptionPane.INFORMATION_MESSAGE); // Can be also JOptionPane.ERROR_MESSAGE
String input = JOptionPane.showInputDialog(message);
int userChoice = JOptionPane.showOptionDialog(
    null,
    "question",
    "header",
    JOptionPane.YES_NO_OPTION,
    JOptionPane.QUESTION_MESSAGE,
    null,
    null,
    null
);
userChoice == JOptionPane.NO_OPTION || userChoice == JOptionPane.YES_OPTION
```

## FXML
### Containers
HBox, VBox, Pane, BorderPane (split into sections), GridPane, BorderPane, TitledPane,

- It is common practice for the top-level container not to be a canvas (maybe VBox for example). 
### Views
ImageView, Label, Text
- Node (mother class of radio button class) has setUserData(), getUserData() methods.
```
@FXML private Canvas canvas;
@FXML private Text text;
@FXML private TextField textfield;
@FXML private Slider slider;
@FXML private Button button;
@FXML private ListView<String> listview;
@FXML private GridPane gridpane;
@FXML private ChoiceBox<String> choicebox;
@FXML private RadioButton radiobutton;

Paint original; 
gc.getFill();
gc.setFill(paint); 
gc.getStroke(); 
gc.setStroke()

GraphicsContext gc = canvas.getGraphicsContext2D();
canvas.getWidth(), canvas.getHeight()
gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
gc.strokeLine(x1, y1, x2, y2);
gc.strokeText(string, x, y);
gc.strokeRect(x, y, w, h);
gc.fillRect(x, y, w, h);
gc.fillOval(x, y, w, h)

message.setText(userMessage);

radiobutton.setSelected(false);

new Circle(centerX, centerY, radius, fill) // Paint fill

// bind example:
textfield.textProperty().bind(field2.valueProperty().asString());

input.valueProperty().addListener(
     new ChangeListener<Number>(){
        public void changed(ObservableValue<? extends Number> observable, Number old, Number new){
            ….
        }
     }
); 

ObservableList<String> values = FXCollections.observableArrayList();
choicebox.setItems(values);
choicebox.setValue(values.get(0));
choicebox.valueProperty().addListener((observable, oldValue, newValue) -> {
choicebox.getValue()
});

appointmentsView.setCellFactory(TextFieldListCell.forListView());

listview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
listview.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

listView.getSelectionModel().selectedItemProperty().addListener(
 new ChangeListener<String>(){
    public void changed(ObservableValue<? extends String> ov,
        String oldValue, String newValue){
        System.out.println(newValue);
    }
 }
); 

@FXML void buttonPressed(ActionEvent event) {
    String id = ((Button) event.getSource()).getId();
}

onMouseClicked
@FXML void clicked(MouseEvent e) {
    event.getX(), event.getY()    // ints
}

Button button = new Button("Done");
button.setId("doneButton")
gridPane.add(button, col, row);
button.setOnAction(method) 

TextInputDialog dialog = new TextInputDialog();
dialog.setTitle("Question");
dialog.setHeaderText("1+1=?");
dialog.setContentText("Answer:");
String s = dialog.showAndWait().orElse("0");
System.out.println(s);

Alert alert = new Alert(Alert.AlertType.INFORMATION);
alert.setTitle("Information");
alert.setHeaderText(null);
alert.setContentText("message");
alert.show();
```
More:
- ToggleGroup
- FileChooser

### Background Task
```
syncThread = new Thread(new Task<Void>() {
    @Override
    protected Void call() {
        return null;
    }
});
syncThread.start();
```

### Main
```
public class MyApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Title");
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("Scene.fxml"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```
# Generic
```
class MyClass<T> {
   private ArrayList<T> items = new ArrayList<T>();
    public void f1(MyClass<T> other) {
    }
    public boolean f2(T item) {
    }
}

public <T extends Comparable<T>> T f3( T[] array) {}

// Give OtherClass or class that inherits from it
public void f4(MyClass<? extends OtherClass> other) {}
```

## Examples
```
interface GetTransformable<T, S> {
    public S transform(T arg) throws Exception;
}
class Convertor implements GetTransformable<ArrayList<String>, Integer> {
    public Integer transform(ArrayList<String> arg) throws Exception {
        int sum = 0;
        for (String item : arg) {
            sum += Integer.parseInt(item);
        }
        return sum;
    }
}

// list that accepts only of class A (2024 94a)
class MyList<T extends A> implements List<A>
```

# Concurrency
```
public class MyThread extends Thread {
    public void run() {
 }
}

(new MyThread()).start()

class MyRunner implements Runnable {
    public void run();
}

(new Thread(new MyRunner())).start();

ExecutorService executor = Executors.newFixedThreadPool(n);
ExecutorService executor = Executors.newCachedThreadPool();
executor.execute(MyRunner);
executor.shutdown();

try {
    executor.awaitTermination(seconds, TimeUnit.SECONDS);
} catch (InterruptedException e) {
    return;
}

try {
    Thread.sleep(milliseconds);
} catch (InterruptedException e) {
    break;
}

try {
    syncThread.join();
} catch (InterruptedException e) {
}

public synchronized void subtract(int amount) {
    while (total <= amount) {
        try {
            wait();
        } catch (InterruptedException e) {
            return;
        }
    }
    total -= amount;
    notifyAll();
}

Lock lock = new ReentrantLock();
lock.lock()
lock.unlock()

Lock lock = new ReentrantLock();
Condition cond = lock.newCondition();
cond.await() // throws InterruptedException
cond.signal()
cond.signalAll()
```
More:
- notify(), notifyAll() - will wake one/all waiting threads on either method, one after the other
  - can be called only from synchronized functions. 
- wait(milliseconds)
- useful class - SafeQueue - private member linkedlist, synchronized insert and pop
- useful class - Event - private member bool, synchronized get and set
- static synchronized - only one thread can run

# Network
```
byte[] data = new byte[]{(byte)i};
```

## URL
```
public URL (String spec) throws MalformedURLException
public URL (String protocol, String host, int port, String file) throws MalformedURLException
public URL (String protocol, String host, String file) throws MalformedURLException
public URL(URL context, String spec) throws MalformedURLException

URL - getProtocol, getHost, getPort, getFile, getRef 
openStream() // returns input stream

openConnection() 
c.getInputStream(), c.getOutputStream()
```

## TCP
```
// Client
socket = new Socket(server, port); // throws UnknownHostException, IOException

// Server
ServerSocket serverSocket = new ServerSocket(PORT); // throws IOException
Socket socket = serverSocket.accept();  // throws IOException

// Common
socket.setSoTimeout(milliseconds); // throws SocketException
in = socket.getInputStream();  // throws IOException
out = socket.getOutputStream(); // throws IOException
in.read()
out.write()
String host = socket.getInetAddress().getHostName;
in.close()
out.close()
socket.close()

PrintWriter out = null;
BufferedReader in = null;
out = new PrintWriter(socket.getOutputStream(), true);
in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
```
## UDP
### Client
```
DatagramSocket socket = new DatagramSocket()

socket.send(new DatagramPacket(data, data.length,  InetAddress.getByName(server), port)); // throws IOException
byte[] data = new byte[1];
DatagramPacket receivePacket = new DatagramPacket(data, data.length);
socket.receive(receivePacket); // throws IOException

socket.close()
```

### Server
```
DatagramSocket socket = new DatagramSocket(PORT);
byte[] data = new byte[1];
DatagramPacket receivePacket = new DatagramPacket(data, data.length);
socket.receive(receivePacket);
socket.send(new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort()));
```

# Note:
- Check if parameters are null or length is 0
- Pay attention to types in signatures (i.e `equals(MyClass) or equals(Object))
- "Exception" must be declared or caught
- Different package → Only accessible through inheritance (i.e., super.variable) (not directly)
- "Dynamic linking" - in inheritance
- Two methods cannot be with same signatures except "static"
- ? cannot be used in generic function signature, can be used in its parameters (i.e ArrayList<? extends String>)
# Marked
```
# General
String.valueOf
arraylist.addAll(arraylist);
ArrayList<String> arraylist = new ArrayList<String>(Arrays.asList(array));
String[] array = arraylist.toArray(new String[0]);

int[] numbers = IntStream.generate(() -> rand.nextInt(bound)).limit(n).toArray();
ArrayList<String> arraylist2 = (ArrayList<String>) Arrays.stream(arraylist.toArray(new MyClass[0])).map(item -> item + "!").collect(Collectors.toCollection(ArrayList::new))); // to convert to array: .toArray(MyClass[]::new)
IntStream.range(begin, end).mapToObj(String::valueOf).toArray(String[]::new)

Button button = new Button("Done");
button.setId("doneButton")
gridPane.add(button, col, row);
button.setOnAction(method) 

syncThread = new Thread(new Task<Void>() {
    @Override
    protected Void call() {
        return null;
    }
});
155 - generic implementation

# TCP
socket = new Socket(server, port); // throws UnknownHostException, IOException
ServerSocket serverSocket = new ServerSocket(PORT); // throws IOException
Socket socket = serverSocket.accept();  // throws IOException
in = socket.getInputStream();  // throws IOException
out = socket.getOutputStream(); // throws IOException
String host = socket.getInetAddress().getHostName;

## UDP
DatagramSocket socket = new DatagramSocket()
socket.send(new DatagramPacket(data, data.length,  InetAddress.getByName(server), port)); // throws IOException
DatagramSocket socket = new DatagramSocket(PORT);
byte[] data = new byte[1];
DatagramPacket receivePacket = new DatagramPacket(data, data.length); // throws IOException
socket.send(new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort()));

# Note:
- (from above)
```