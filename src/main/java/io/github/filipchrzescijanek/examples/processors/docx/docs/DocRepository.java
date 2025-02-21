package io.github.filipchrzescijanek.examples.processors.docx.docs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DocRepository {

    @PersistenceContext
    EntityManager em;

    public List<Doc> getAll() {
        return em.createQuery("SELECT b FROM Doc b", Doc.class)
                .getResultList();
    }

    public Doc getById(Long id) {
        return Optional.ofNullable(em.find(Doc.class, id))
                .orElseThrow(() -> new NotFoundException("Doc with id " + id + " not found"));
    }

    @Transactional
    public void create(@Valid Doc doc) {
        em.persist(doc);
    }

    @Transactional
    public Doc update(Long id, @Valid Doc doc) {
        Doc existing = em.find(Doc.class, id);
        if (existing == null) {
            throw new NotFoundException("Doc with id " + id + " not found");
        }
        existing.setAuthor(doc.getAuthor());
        existing.setTitle(doc.getTitle());
        existing.setContents(doc.getContents());
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        Doc doc = em.find(Doc.class, id);
        if (doc == null) {
            throw new NotFoundException("Doc with id " + id + " not found");
        }
        em.remove(doc);
    }

}
