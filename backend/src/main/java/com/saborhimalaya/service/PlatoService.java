package com.saborhimalaya.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saborhimalaya.dto.CrearPlatoRequest;
import com.saborhimalaya.dto.PlatoDto;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.repository.PlatoRepository;

@Service
public class PlatoService {

    private final PlatoRepository platoRepository;

    public PlatoService(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    public List<PlatoDto> obtenerPlatosDeLaCarta() {
        return platoRepository.findByEnCartaTrueOrderByCategoriaAscNombreAsc().stream()
                .map(PlatoDto::desde)
                .toList();
    }

    public PlatoDto anadirPlatoALaCarta(CrearPlatoRequest request) {
        Plato plato = new Plato(request.getNombre(), request.getDescripcion(), request.getPrecio(), request.getCategoria(), true);
        return PlatoDto.desde(platoRepository.save(plato));
    }
    public Plato anadirPlato(Plato plato){
        return platoRepository.save(plato);
    }
}
