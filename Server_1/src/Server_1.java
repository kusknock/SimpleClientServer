import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server_1 {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8009)) {

            System.out.println("Server Started!");

            while (true) {
                try {
                    Socket socket = server.accept();
                    PrintWriter writer = new PrintWriter(socket.getOutputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    System.out.println("Client connected!");

                    new Thread(() -> {

                        try {

                            String requestSnils = reader.readLine();

                            System.out.println(requestSnils);

                            String response = validateSnils(requestSnils) ? "SNILS is correct" : "SNILS is not correct";

                            System.out.println(response);

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            writer.println(response);
                            writer.flush();

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

    public static boolean validateSnils(String snils) {

        if (!snils.matches("\\d{3}-\\d{3}-\\d{3} \\d{2}"))
            return false;

        snils = snils.replace("-", "");
        snils = snils.replace(" ", "");

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
        var checkDigit = 0;

        for (var i = 0; i < 9; i++)
            sum += Character.getNumericValue(snils.charAt(i)) * (9 - i);

        if (sum < 100)
            checkDigit = sum;
        else if (sum > 101) {
            checkDigit = sum % 101;
            if (checkDigit == 100) {
                checkDigit = 0;
            }
        }

        return checkDigit == Integer.parseInt(snils.substring(9));
    }
}
