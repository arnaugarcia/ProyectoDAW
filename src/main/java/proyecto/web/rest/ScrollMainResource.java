package proyecto.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import proyecto.domain.*;
import proyecto.domain.DTO.MainScrollDTO;
import proyecto.repository.*;
import proyecto.security.SecurityUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ScrollMainResource {

    @Inject
    UserExtRepository userExtRepository;

    @Inject
    OfferRepository offerRepository;

    @Inject
    PhotoRepository photoRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    FollowingRepository followingRepository;

    @Inject
    BloquedRepository bloquedRepository;

    @RequestMapping(value = "/main/scroll",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<List<MainScrollDTO>> MainScroll(){

        UserExt userExt = userExtRepository.
            findByUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());

        //TODO obtener fotos donde la puntuacion de la foto sea mayor a 3
        //TODO Añadira UserExt el campo bloqueado

        List<Photo> photos = photoRepository.findUserExtPopularGreaterThan(userExt.getCity())
            //TODO Codigo para buscar los usuarios bloqueados.
            .parallelStream()
            .peek(photo -> System.out.println("antes del filtro" + photo))
            .filter(photo ->
                bloquedRepository.findByBlock(userExt.getUser())
                    .stream()
                    .peek(bloqued -> System.out.println("usuario bloqueado (bloqued)" + bloqued))
                    .map(Bloqued::getBlocked)
                    .peek(user -> System.out.println("usuario bloqueado " + user))
                    .noneMatch(user -> user.equals(photo.getUser())))
                    .peek(photo -> System.out.println("despues del filtro" + photo))
            .collect(Collectors.toList());


        List<User> followingUsers = followingRepository.SelectFollowingFindByFollower(userExt.getUser());

        List<Offer> offer = offerRepository.findOfferOrderByDateAndNotClosed();



        List<MainScrollDTO> scroll = new ArrayList<>();

        int j = 0;
        int ran = (int) (Math.random() * 4) + 2;

        for(Photo photo : photos){
            MainScrollDTO main = new MainScrollDTO();

            if(j == ran && !offer.isEmpty()){
                main.setOffer(offer.get(0));
                scroll.add(main);
                offer.remove(0);

                j = 0;
            }else{
                main.setPhoto(photo);
                main.setUserExt(photo.getUser().getUserExt());
                scroll.add(main);
                j++;
            }
        }

        return new ResponseEntity<>(
            scroll,
            HttpStatus.OK);
    }
}
