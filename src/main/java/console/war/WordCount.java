package console.war;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class WordCount {

    private static final int TOTAL_PAGES = 600;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static void main(String[] args) {

        List<String> keyWords = Arrays.asList(
                "direct",
                "downgrade",
                "humo",
                "windows",
                "linux",
                "macos",
                "steamos",
                "game pass",
                "gamepass",
                "tito phil",
                "titophil",
                "phil",
                "monopolio",
                "sonydios",
                "sony dios",
                "dios",
                "raytracing",
                "hasan",
                "steam",
                "pc",
                "microsoft",
                "xbox",
                "sony",
                "playstation",
                "nintendo",
                "switch",
                "epicgames",
                "epic games",
                "epicstore",
                "epic store",
                "tito gabe",
                "lord gaben",
                "puto gordo",
                "nft",
                "star citizen",
                "take my money"
                );
        


        System.out.println("Revisadas " + TOTAL_PAGES + " paginas por hilo" );
        countKeyWords(
                3152,
                "https://vandal.elespanol.com/foro/mensaje/890262/tema-oficial-taberna-gordopecera-hoy-en-espejo-publico-steam-se-va-a-la-mierda/",
                keyWords, "PC");

        countKeyWords(
                3090,
                "https://vandal.elespanol.com/foro/mensaje/974278/hilo-oficioso-xbox-next-gen-rumores-y-noticias-anaconda-roma-anthem-/",
                keyWords, "Xbox");

        countKeyWords(
                8985,
                "https://vandal.elespanol.com/foro/mensaje/970857/-playstation-5-rumores-y-filtraciones-/",
                keyWords, "Playstation");

        countKeyWords(
                10743,
                "https://vandal.elespanol.com/foro/mensaje/917132/the-evolved-tabernaka-de-nx-age-of-kimishima-edition/",
                keyWords, "Switch");
    }

    private static void countKeyWords(int latestPage, String url, List<String> keyWords, String threadName) {
        System.out.println("--------------------");
        int until = latestPage - TOTAL_PAGES;
        System.out.println("Hilo " + threadName + " desde la pagina " + until + " hasta " + latestPage);
        Map<String, Double> wordsCount = new HashMap<>();
        for (int pageNumber = latestPage; pageNumber > until; pageNumber--) {
            String webContent = getWebContent(url + pageNumber);
            for (String word : keyWords) {
                int count = StringUtils.countMatches(webContent, word);
                double currentCount = wordsCount.get(word) == null ? 0 : wordsCount.get(word);
                wordsCount.put(word, currentCount + count);
            }
        }

        List<Map.Entry<String, Double>> result = wordsCount.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Collections.reverse(result);
        int index = 1;
        for (Map.Entry<String, Double> entry : result) {
            String word = entry.getKey();
            Double count = entry.getValue();
            if (count > 0) {
                double perPage = count / TOTAL_PAGES;
                System.out.println(index + " - " + word + " aparece " + count + " veces, " + DECIMAL_FORMAT.format(perPage) + " veces por pagina" );
                index++;
            }
        }
    }

    private static String getWebContent(String url) {
        try {
            String forumText = Jsoup.connect(url)
                    .get()
                    .select("div.principalforo")
                    .text()
                    .toLowerCase()
                    .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ");
            return textNormalizer(forumText);
        } catch (IOException e) {
            System.out.println("Error loading " + url);
            e.printStackTrace();
            return "";
        }
    }

    public static String textNormalizer(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}
