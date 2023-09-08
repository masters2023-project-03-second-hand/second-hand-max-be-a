package codesquard.support;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SupportRepository {

	@Autowired
	private EntityManager em;

	public <T> T save(T entity) {
		em.persist(entity);
		em.flush();
		em.clear();
		return entity;
	}

	public <T> T findById(Long id, Class<T> entityClass) {
		return em.find(entityClass, id);
	}

	public <T> List<T> findAll(Class<T> entityClass) {
		return em.createQuery("select entity from " + entityClass.getSimpleName() + " entity").getResultList();
	}
}
