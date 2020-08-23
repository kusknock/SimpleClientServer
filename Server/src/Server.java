import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8000)) {

            System.out.println("Server Started!");

            while (true) {
                try {

                    Socket socket = server.accept();
                    PrintWriter writer =
                            new PrintWriter(socket.getOutputStream());
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream()));

                    System.out.println("Client connected!");


                    new Thread(() -> {

                        System.out.println();
                        boolean checkSnils = false;
                        ArrayList<String> rightGetRequests = new ArrayList<>();

                        try {
                            while (reader.ready()) {

                                String line = reader.readLine();

                                String request = line.split(" ", 2)[0].equals("GET") ? line : null;

                                if(request != null) rightGetRequests.add(request);

                                System.out.println(line);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        writer.println("HTTP/1.1 200 OK");
                        writer.println("Content-Type: text/html; charset=utf-8");
                        writer.println();
                        writer.println("<p>Введите номер страхового свидетельства!</p>");
                        writer.println("<form method=\"get\">\n" +
                                "  <label for=\"snils\">СНИЛС:</label><br>\n" +
                                "  <input type=\"text\" id=\"snils\" name=\"snils\" value=\"\"><br><br>\n" +
                                "  <input type=\"submit\" value=\"Отправить\">\n" +
                                "</form>");

                        boolean existsSnilsInGetRequest = false;

                        String snils;

                        for (String rightRequest : rightGetRequests) {

                            snils = parseSnils(rightRequest);

                            if (snils == null) continue;

                            existsSnilsInGetRequest = true;

                            checkSnils = validateSnils(snils);
                        }

                        if(existsSnilsInGetRequest)
                            if (checkSnils) writer.println("<p style=\"color:green;\">Номер введен корректно!</p>");
                            else writer.println("<p style=\"color:red;\">Не верный формат номера!</p>");

                        writer.flush();


                        try {
                            reader.close();
                            writer.close();
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println("Client disconnected!");

                    }).start();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseSnils(String str) {
        try {
            return str.split("\\?")[1].split(" ")[0].split("=")[1];

        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static boolean validateSnils(String str) {
        String snils = str;

        if (!snils.matches("\\d{3}-\\d{3}-\\d{3}\\+\\d{2}"))
            return false;

        snils = snils.replace("-", "");
        snils = snils.replace("+", "");

        int previous;
        int current;
        int countCoincidence = 1;

        for (int i = 1; i < 11; i++) {

            previous = Character.getNumericValue(snils.charAt(i - 1));

            current = Character.getNumericValue(snils.charAt(i));

            countCoincidence += previous == current ? 1 : 0;

            if (countCoincidence > 3) return false;
        }

        var sum = 0;

        for (var i = 0; i < 9; i++) {
            sum += Character.getNumericValue(snils.charAt(i)) * (9 - i);
        }
        var checkDigit = 0;

        if (sum < 100) {
            checkDigit = sum;
        } else if (sum > 101) {
            checkDigit = sum % 101;
            if (checkDigit == 100) {
                checkDigit = 0;
            }
        }

        return checkDigit == Integer.parseInt(snils.substring(9));
    }
}
