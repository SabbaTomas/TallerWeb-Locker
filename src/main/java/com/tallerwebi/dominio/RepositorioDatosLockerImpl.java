package com.tallerwebi.dominio;

import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.TipoLocker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

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
    public Locker actualizar(Locker locker) {
        Session session = sessionFactory.getCurrentSession();
        session.update(locker);
        return locker;
    }

    @Override
    public void eliminar(Long idLocker) {
        Session session = sessionFactory.getCurrentSession();
        Locker locker = session.get(Locker.class, idLocker);
        if (locker != null) {
            session.delete(locker);
        }
    }

    @Override
    public Locker obtenerPorId(Long idLocker) {
        return (Locker) this.sessionFactory.getCurrentSession()
                .createQuery("FROM Locker WHERE id = :id")
                .setParameter("id", idLocker)
                .uniqueResult();
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
    public List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal) {
        String hql = "FROM Locker WHERE codigo_postal = :codigo_postal";
        return sessionFactory.getCurrentSession().createQuery(hql, Locker.class)
                .setParameter("codigo_postal", codigoPostal)
                .list();
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