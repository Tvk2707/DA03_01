package BE.dao.impl;

import BE.dao.GenericDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO chung cài đặt GenericDao cho tất cả entity
 * Xử lý CRUD và các thao tác chung với database
 */
public class GenericDaoImpl<T, ID> implements GenericDao<T, ID> {
    
    private final Class<T> entityClass;
    
    /**
     * Constructor nhận Class<T> để xử lý Java generic erasure
     * @param entityClass Lớp entity cần quản lý
     */
    public GenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    /**
     * Lưu entity mới
     */
    @Override
    public T save(T entity) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lưu dữ liệu", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Cập nhật entity
     */
    @Override
    public T update(T entity) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            T mergedEntity = em.merge(entity);
            em.getTransaction().commit();
            return mergedEntity;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật dữ liệu", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Xóa entity
     */
    @Override
    public void delete(T entity) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            T managedEntity = em.find(entityClass, entity);
            if (managedEntity != null) {
                em.remove(managedEntity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa dữ liệu", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Xóa entity theo ID
     */
    @Override
    public void deleteById(ID id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa dữ liệu theo ID", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Tìm entity theo ID
     */
    @Override
    public T findById(ID id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            return em.find(entityClass, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm dữ liệu theo ID", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Lấy tất cả entity
     */
    @Override
    public List<T> findAll() {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy toàn bộ dữ liệu", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Lấy entity có phân trang
     */
    @Override
    public List<T> findWithPaging(int pageNumber, int pageSize) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            
            int offset = (pageNumber - 1) * pageSize;
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);
            
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy dữ liệu với phân trang", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Đếm tổng số entity
     */
    @Override
    public long count() {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi đếm dữ liệu", e);
        } finally {
            em.close();
        }
    }
}
