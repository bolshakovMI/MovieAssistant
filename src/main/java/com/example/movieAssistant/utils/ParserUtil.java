package com.example.movieAssistant.utils;

import com.example.movieAssistant.exceptions.ParsingException;
import com.example.movieAssistant.model.dto.request.WishRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ParserUtil {

    public static WishRequest getWishRequestByImdbId(String imdbIdString, long imdbIdLong) {
        Document doc = getDocument(imdbIdString);
        String name = getTitle(doc);
        Short year = getYear(doc);
        Double rating = getRating(doc);
        List<String> genreNames = getGenres(doc);

        return new WishRequest(imdbIdLong, name, year, rating, genreNames);
    }

    private static Document getDocument(String imdb_id) {
        Document doc;

        try {
            doc = Jsoup.connect("https://www.imdb.com/title/tt" + imdb_id + "/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();
        } catch (SocketTimeoutException e) {
            throw new ParsingException("Истекло время ожидания ответа", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new ParsingException("Ошибка при чтении страницы", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return doc;
    }

    private static String getTitle(Document doc) {
        Element titleElement =
                //doc.select("div.sc-d8941411-1.jbtHqy").first();
          doc.select("h1[data-testid=hero__pageTitle] + div").first();
//          doc.select("div#__next div.iPPbjm > div").first();
        if (titleElement==null) {
            throw new ParsingException("Имя фильма не найдено при парсинге", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String originalTitle = titleElement.text();
        if (originalTitle.length()<17 || !originalTitle.startsWith("Original title: ")) {
            throw new ParsingException("Ошибка указателя для парсинга имени фильма", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return originalTitle.substring(16);
    }

    private static Short getYear(Document doc) {
        short year;

        try {
            Element yearElement = doc.select("a[href*=releaseinfo]").first();
//          doc.select("div#__next div.iPPbjm > ul > li:nth-child(1) > a").first();
            String yearString = yearElement.text();
            year = Short.parseShort(yearString.substring(0,4));
        } catch (NumberFormatException e) {
            throw new ParsingException("Ошибка при парсинге строки с информацией о годе выпуска в число", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e) {
            throw new ParsingException("Ошибка указателя для парсинга года выпуска фильма", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (year < 1895 || year > 2049) {
            throw new ParsingException("Результат парсинга года выпуска фильма получен не в заданном формате: " + year, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return year;
    }

    private static Double getRating(Document doc) {
        double rating;

        try {
            Element ratingElement = doc.select("div[data-testid=\"hero-rating-bar__aggregate-rating__score\"] > span:nth-child(1)").first();
            String ratingString = ratingElement.text();
            rating = Double.parseDouble(ratingString);
        } catch (NumberFormatException e) {
            throw new ParsingException("Ошибка при парсинге строки с информацией о рейтинге в число", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e) {
            throw new ParsingException("Ошибка указателя для парсинга рейтинга фильма", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (rating < 1.0 || rating > 10.0) {
            throw new ParsingException("Результат парсинга рейтинга фильма получен не в заданном формате: " + rating, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return rating;
    }

    private static List<String> getGenres(Document doc) {
        List<String> genreNames;

        try {
            Elements genres = doc.select("[data-testid=\"interests\"] .ipc-chip__text");
            genreNames = new ArrayList<>();
            for (Element genre : genres) {
                genreNames.add(genre.text());
            }
        } catch (NullPointerException e) {
            throw new ParsingException("Ошибка указателя для парсинга жанров фильма", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return genreNames;
    }
}
