package com.diego.moviecatalogservice.resources;


import com.diego.moviecatalogservice.models.CatalogItem;
import com.diego.moviecatalogservice.models.Movie;
import com.diego.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    // Get RestTemplate Bean
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    // Get WebClient.Builder Bean
    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        // Get all rated movies IDs
        UserRating ratings = restTemplate.getForObject("http://rating-data-service/ratingsdata/users/" + userId, UserRating.class);

        // Replace each rating with the catalog ID
        return ratings.getUserRating().stream().map(rating -> {

            // For each movie ID, call movie info service and get details about
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            // Put them all together
            return new CatalogItem(movie.getName(), "Test", rating.getRating());
        }).collect(Collectors.toList());





    }

}


            /*
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
             */
