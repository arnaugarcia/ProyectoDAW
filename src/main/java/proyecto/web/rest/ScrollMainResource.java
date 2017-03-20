package proyecto.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import proyecto.domain.DTO.MainScrollDTO;
import proyecto.domain.Offer;
import proyecto.domain.Photo;
import proyecto.domain.User;
import proyecto.domain.UserExt;
import proyecto.repository.OfferRepository;
import proyecto.repository.PhotoRepository;
import proyecto.repository.UserExtRepository;
import proyecto.repository.UserRepository;
import proyecto.security.SecurityUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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

    @RequestMapping(value = "/main/scroll",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<List<MainScrollDTO>> MainScroll(){

        UserExt userExt = userExtRepository.
            findByUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());

        //TODO obtener fotos donde la puntuacion del usuario sea mayor a 3

        List<Photo> photos = photoRepository.findUserExtPopularGreaterThan(userExt.getCity());

        List<Offer> offer = offerRepository.findOfferOrderByDate();

        List<MainScrollDTO> scroll = new ArrayList<>();

        int j = 0;
        int ran = (int) (Math.random() * 2) + 7;

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
            }
        }

        return new ResponseEntity<>(
            scroll,
            HttpStatus.OK);
    }
}
