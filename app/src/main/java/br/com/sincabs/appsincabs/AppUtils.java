package br.com.sincabs.appsincabs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class AppUtils {

    static public String limparCPF(String cpf) {

        return cpf.replaceAll("[^x0-9]", "");
    }

    static public String formatarCPF(String cpf) {

        String result;

        if (cpf.length() == 11) {

            result = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
        }
        else if (cpf.length() == 10) {

            result = "0" + cpf.substring(0, 2) + "." + cpf.substring(2, 5) + "." + cpf.substring(5, 8) + "-" + cpf.substring(8, 10);
        }
        else if (cpf.length() == 9) {

            result = "00" + cpf.substring(0, 1) + "." + cpf.substring(1, 4) + "." + cpf.substring(4, 7) + "-" + cpf.substring(7, 9);
        }
        else if (cpf.length() == 8) {

            result = "000." + cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "-" + cpf.substring(6, 8);
        }
        else {

            result = cpf;
        }

        return result;
    }

    public static String crc32(String input) {

        byte[] bytes = input.getBytes();

        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);

        return Long.toString(checksum.getValue());
    }

    static public boolean validarCPF(String cpfText) {

        boolean ret = false;
        String base = "000000000";
        String digitos = "00";

        String cpf = cpfText.replaceAll("[^0-9]", "");

        if (cpf.length() == 0) return false;

        if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333") || cpf.equals("44444444444") ||
                cpf.equals("55555555555") || cpf.equals("66666666666") || cpf.equals("77777777777") || cpf.equals("88888888888") || cpf.equals("99999999999")) {

            return false;
        }

        if (cpf.length() <= 11) {

            if (cpf.length() < 11) {

                cpf = base.substring(0, 11 - cpf.length()) + cpf;
                base = cpf.substring(0, 9);
            }

            base = cpf.substring(0, 9);
            digitos = cpf.substring(9, 11);

            int soma = 0, mult = 11;
            int[] var = new int[11];


            for (int i = 0; i < 9; i++) {

                var[i] = Integer.parseInt("" + cpf.charAt(i));

                if (i < 9) {

                    soma += (var[i] * --mult);
                }
            }

            int resto = soma % 11;

            if (resto < 2) {

                var[9] = 0;
            }
            else {

                var[9] = 11 - resto;
            }

            soma = 0;
            mult = 11;

            for (int i = 0; i < 10; i++) {

                soma += var[i] * mult--;
            }

            resto = soma % 11;

            if (resto < 2) {

                var[10] = 0;
            }
            else {

                var[10] = 11 - resto;
            }

            if ((digitos.substring(0, 1).equalsIgnoreCase(Integer.toString(var[9]))) && (digitos.substring(1, 2).equalsIgnoreCase(Integer.toString(var[10])))) {

                ret = true;
            }
        }

        return ret;
    }

    public static boolean validarNome(String name) {

        if (name.length() < 5) return false;
        if (name.split(" ").length < 2) return false;

        String regx = "^[\\p{L} .'-]+$";

        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);

        return matcher.find();
    }

    public static boolean validarEmail(String email) {

        return email.length() > 0 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validarMatricula(String matricula) {

        return matricula.length() >= 4;
    }

    public static String formatarTexto(String text) {

        String result = text.replaceAll("(?m)(^ *| +(?= |$))", "").replaceAll("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+", "$1");

        return result.trim();
    }
}
