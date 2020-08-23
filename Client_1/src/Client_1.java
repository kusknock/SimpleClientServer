import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client_1 {
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                try (
                        Socket socket = new Socket("127.0.0.1", 8009);
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {
                    System.out.println("Connection to server...");
                    System.out.println();

                    System.out.println("Please enter your correct SNILS:");

                    if (!scanner.hasNextLine()) {
                        System.out.println("Enter correct number");
                        continue;
                    }

                    String input = scanner.nextLine();

                    writer.println(input);
                    writer.flush();

                    String response = reader.readLine();
                    System.out.println(response);

                    System.out.println("Please enter any number for continue:");

                    if (!scanner.hasNextInt()) {
                        System.out.println("Exit!");
                        break;
                    }

                    scanner.nextLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
