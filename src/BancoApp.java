import java.io.*; // Importa las clases necesarias para la entrada/salida de archivos
import java.util.ArrayList; // Importa la clase ArrayList
import java.util.HashMap; // Importa la clase HashMap
import java.util.List; // Importa la interfaz List
import java.util.Map; // Importa la interfaz Map
import java.util.Scanner; // Importa la clase Scanner para la entrada del usuario

public class BancoApp {
    // Mapa para almacenar las cuentas, usando el número de cuenta como clave
    private static Map<String, Cuenta> cuentas = new HashMap<>();
    // Ruta del archivo donde se guardarán las cuentas
    private static final String ARCHIVO_CUENTAS = "C:\\Users\\willi\\Desktop\\BancoApp\\src\\cuentas.txt";

    public static void main(String[] args) {
        // Carga las cuentas desde el archivo al iniciar el programa
        cargarCuentas();
        Scanner scanner = new Scanner(System.in);

        // Muestra el menú principal
        System.out.println("***********************************");
        System.out.println("    Bienvenido a BancoApp    ");
        System.out.println("***********************************");
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");

        int opcion = scanner.nextInt();

        // Maneja la opción seleccionada por el usuario
        switch (opcion) {
            case 1:
                iniciarSesion();
                break;
            case 2:
                registrarse();
                break;
            case 3:
                guardarCuentas(); // Guarda las cuentas antes de salir
                System.out.println("Gracias por usar BancoApp. ¡Hasta luego!");
                break;
            default:
                System.out.println("Opción no válida. Por favor, seleccione 1, 2 o 3.");
        }
    }

    public static void iniciarSesion() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su número de cuenta: ");
        String numeroCuenta = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contraseña = scanner.nextLine();

        // Verifica las credenciales del usuario
        Cuenta cuenta = cuentas.get(numeroCuenta);
        if (cuenta != null && cuenta.getContraseña().equals(contraseña)) {
            System.out.println("Inicio de sesión exitoso.");
            mostrarMenuPrincipal(cuenta);
        } else {
            System.out.println("Credenciales incorrectas. Intente de nuevo.");
        }
    }

    public static void registrarse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese su número de cuenta: ");
        String numeroCuenta = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contraseña = scanner.nextLine();

        // Verifica si el número de cuenta ya existe
        if (cuentas.containsKey(numeroCuenta)) {
            System.out.println("El número de cuenta ya existe. Intente con otro.");
        } else {
            // Crea una nueva cuenta y la añade al mapa
            cuentas.put(numeroCuenta, new Cuenta(nombre, numeroCuenta, contraseña));
            System.out.println("Registro exitoso. Ahora puede iniciar sesión.");
            guardarCuentas(); // Guarda las cuentas después del registro
        }
    }

    public static void mostrarMenuPrincipal(Cuenta cuenta) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        // Bucle para el menú principal después de iniciar sesión
        while (continuar) {
            System.out.println("\n***********************************");
            System.out.println("        Menú Principal        ");
            System.out.println("***********************************");
            System.out.println("Número de Cuenta: " + cuenta.getNumeroCuenta());
            System.out.println("Nombre: " + cuenta.getNombre());
            System.out.println("Saldo: $" + cuenta.getSaldo());
            System.out.println("1. Consignar");
            System.out.println("2. Retirar");
            System.out.println("3. Ver Movimientos");
            System.out.println("4. Cerrar Sesión");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();

            // Maneja la opción seleccionada en el menú principal
            switch (opcion) {
                case 1:
                    cuenta.setSaldo(consignar(cuenta));
                    guardarCuentas(); // Guarda las cuentas después de consignar
                    break;
                case 2:
                    cuenta.setSaldo(retirar(cuenta));
                    guardarCuentas(); // Guarda las cuentas después de retirar
                    break;
                case 3:
                    verMovimientos(cuenta);
                    break;
                case 4:
                    System.out.println("Cerrando sesión...");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione 1, 2, 3 o 4.");
            }
        }
    }

    public static double consignar(Cuenta cuenta) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad a consignar: ");
        double cantidad = scanner.nextDouble();
        cuenta.addMovimiento("Consignación: $" + cantidad); // Añade movimiento
        double saldo = cuenta.getSaldo() + cantidad;
        System.out.println("Consignación exitosa. Nuevo saldo: $" + saldo);
        return saldo;
    }

    public static double retirar(Cuenta cuenta) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad a retirar: ");
        double cantidad = scanner.nextDouble();
        if (cantidad <= cuenta.getSaldo()) {
            cuenta.addMovimiento("Retiro: $" + cantidad); // Añade movimiento
            double saldo = cuenta.getSaldo() - cantidad;
            System.out.println("Retiro exitoso. Nuevo saldo: $" + saldo);
            return saldo;
        } else {
            System.out.println("Saldo insuficiente.");
            return cuenta.getSaldo();
        }
    }

    public static void verMovimientos(Cuenta cuenta) {
        System.out.println("Movimientos de la cuenta:");
        for (String movimiento : cuenta.getMovimientos()) {
            System.out.println(movimiento);
        }
    }

    public static void cargarCuentas() {
        File file = new File(ARCHIVO_CUENTAS);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_CUENTAS))) {
                cuentas = (HashMap<String, Cuenta>) ois.readObject();
                System.out.println("Cuentas cargadas exitosamente.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error al cargar las cuentas: " + e.getMessage());
            }
        } else {
            System.out.println("El archivo de cuentas no existe. Se creará uno nuevo al guardar.");
        }
    }

    public static void guardarCuentas() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CUENTAS))) {
            oos.writeObject(cuentas);
            System.out.println("Cuentas guardadas exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar las cuentas: " + e.getMessage());
        }
    }
}

// Clase que representa una cuenta bancaria
class Cuenta implements Serializable {
    private String nombre; // Nombre del titular de la cuenta
    private String numeroCuenta; // Número de la cuenta
    private String contraseña; // Contraseña de la cuenta
    private double saldo; // Saldo de la cuenta
    private List<String> movimientos; // Lista de movimientos de la cuenta

    // Constructor de la clase Cuenta
    public Cuenta(String nombre, String numeroCuenta, String contraseña) {
        this.nombre = nombre;
        this.numeroCuenta = numeroCuenta;
        this.contraseña = contraseña;
        this.saldo = 0.0; // Saldo inicial
        this.movimientos = new ArrayList<>(); // Inicializa la lista de movimientos
    }

    // Métodos getter y setter para acceder y modificar los atributos privados
    public String getNombre() {
        return nombre;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public String getContraseña() {
        return contraseña;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void addMovimiento(String movimiento) {
        movimientos.add(movimiento);
    }

    public List<String> getMovimientos() {
        return movimientos;
    }
}