package com.tallerwebi.infraestructura.locker;

import com.tallerwebi.util.Haversine;
import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.TipoLocker;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RepositorioDatosLockerImpl implements RepositorioDatosLocker {

    private final SessionFactory sessionFactory;

    public RepositorioDatosLockerImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void guardar(Locker locker) {
        this.sessionFactory.getCurrentSession().save(locker);
    }

    @Override
    public Locker obtenerLockerPorId(Long idLocker) {
        Locker locker = (Locker) this.sessionFactory.getCurrentSession()
                .createQuery("FROM Locker WHERE id = :id")
                .setParameter("id", idLocker)
                .uniqueResult();
        if (locker == null) {
            throw new IllegalArgumentException("Locker no encontrado");
        }
        return locker;
    }


    @Override
    public List<Locker> obtenerLockersPorTipo(TipoLocker tipoLocker) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("FROM Locker WHERE tipo = :tipoLocker", Locker.class)
                .setParameter("tipoLocker", tipoLocker)
                .getResultList();
    }

    @Override
    public List<Locker> obtenerSeleccionados() {
        String hql = "FROM Locker WHERE seleccionado = true";
        return sessionFactory.getCurrentSession().createQuery(hql, Locker.class).list();
    }

    @Override
    public void eliminarTodos() {
        String hql = "DELETE FROM Locker";
        sessionFactory.getCurrentSession().createQuery(hql).executeUpdate();
    }

    @Override
    public void actualizar(Long idLocker, TipoLocker tipoLocker) {
        this.sessionFactory.getCurrentSession()
                .createQuery("UPDATE Locker SET tipo = :tipoLocker WHERE id = :idLocker")
                .setParameter("tipoLocker", tipoLocker)
                .setParameter("idLocker", idLocker)
                .executeUpdate();
    }

    @Override
    public void eliminar(Long idLocker) {
        boolean seleccionado = false;
        this.sessionFactory.getCurrentSession()
                .createQuery("UPDATE Locker SET seleccionado = :seleccionado WHERE id = :idLocker")
                .setParameter("idLocker", idLocker)
                .setParameter("seleccionado", seleccionado)
                .executeUpdate();
    }


    @Override
    public List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal) {
        String hql = "FROM Locker WHERE codigo_postal = :codigo_postal AND seleccionado = true";
        return sessionFactory.getCurrentSession().createQuery(hql, Locker.class)
                .setParameter("codigo_postal", codigoPostal)
                .list();
    }

    @Override
    public List<Locker> encontrarLockersPorCercania(double latitude, double longitude, double maxDistance) {
        double rangoLat = maxDistance / 111.0;
        double rangoLon = maxDistance / (111.0 * Math.cos(Math.toRadians(latitude)));

        List<Locker> lockersEnRango = obtenerLockersPorRangoDeCoordenadas(latitude - rangoLat, latitude + rangoLat, longitude - rangoLon, longitude + rangoLon);
        List<Locker> lockersCercanos = new ArrayList<>();

        for (Locker locker : lockersEnRango) {
            double distancia = Haversine.distance(latitude, longitude, locker.getLatitud(), locker.getLongitud());
            if (distancia < maxDistance) {
                lockersCercanos.add(locker);
            }
        }

        return lockersCercanos;
    }

    @Override
    public List<Locker> obtenerLockersPorRangoDeCoordenadas(double latMin, double latMax, double lonMin, double lonMax) {
        String hql = "FROM Locker WHERE latitud BETWEEN :latMin AND :latMax AND longitud BETWEEN :lonMin AND :lonMax";
        return sessionFactory.getCurrentSession().createQuery(hql, Locker.class)
                .setParameter("latMin", latMin)
                .setParameter("latMax", latMax)
                .setParameter("lonMin", lonMin)
                .setParameter("lonMax", lonMax)
                .getResultList();
    }


}
/*
$places = Places::where([
        ['visible', '=', '1'],
        ['deleted', '=', '0'],
        ])->select(['id',
        'name',
        'description',
        'latitude',
        'longitude',
        'deleted',
        DB::raw('concat("' . env('APP_URL') . '", avatar_url) as avatarUrl'),
        'user_id as userId',
        'visible',
        'address',
        DB::raw('(6351 * acos( cos( radians(' . $latitude . ') ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians(' . $langitude . ') ) + sin( radians(' . $latitude . ') ) * sin(radians(latitude)) ) ) AS distance')
        ])
        ->orderBy('distance')
        ->havingRaw('distance < ' . env("PLACES_SEARCH_DISTANCE"))
        ->take(env("LIMIT_SEARCH_DISTANCE"))
        ->get()
        ->toArray();


 */