package com.hjwylde.rivers.db.services;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.hjwylde.rivers.db.models.ImageDocument;
import com.hjwylde.rivers.db.models.SectionDocument;
import com.hjwylde.rivers.db.views.SectionsView;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import java.util.Collections;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import static java.util.Objects.requireNonNull;

public final class CouchbaseRepository implements Repository {
    // TODO (hjw): move logic inside reactivex components and enable disposing of resources / cancelling actions

    @NonNull
    private final Database mDatabase;

    private CouchbaseRepository(@NonNull Database database) {
        mDatabase = requireNonNull(database);
    }

    @NonNull
    @Override
    public Single<Image> createImage(@NonNull Image.Builder builder) {
        ImageDocument.Builder documentBuilder = ImageDocument.builder(mDatabase).copy(builder);

        try {
            ImageDocument imageDocument = documentBuilder.create();

            return Single.just(imageDocument);
        } catch (CouchbaseLiteException e) {
            return Single.error(e);
        }
    }

    @NonNull
    @Override
    public Single<Section> createSection(@NonNull Section.Builder builder) {
        SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

        try {
            SectionDocument sectionDocument = documentBuilder.create();

            return Single.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Single.error(e);
        }
    }

    @NonNull
    @Override
    public Completable deleteSection(@NonNull Section section) {
        Document document = mDatabase.getExistingDocument(section.getId());

        try {
            if (document != null) {
                document.delete();
            }

            return Completable.complete();
        } catch (CouchbaseLiteException e) {
            return Completable.error(e);
        }
    }

    @NonNull
    @Override
    public Maybe<Image> getImage(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            ImageDocument imageDocument = new ImageDocument(document);

            return Maybe.just(imageDocument);
        } else {
            return Maybe.empty();
        }
    }

    @NonNull
    @Override
    public Maybe<Section> getSection(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            SectionDocument sectionDocument = new SectionDocument(document);

            return Maybe.just(sectionDocument);
        } else {
            return Maybe.empty();
        }
    }

    @NonNull
    @Override
    public Observable<Section> getSections() {
        View view = SectionsView.getInstance(mDatabase);

        return Observable
                .<Document>create(emitter -> {
                    try {
                        QueryEnumerator result = view.createQuery().run();

                        for (QueryRow row : result) {
                            emitter.onNext(row.getDocument());
                        }

                        emitter.onComplete();
                    } catch (CouchbaseLiteException e) {
                        emitter.onError(e);
                    }
                })
                .map(SectionDocument::new);
    }

    @NonNull
    @Override
    public Flowable<Section> streamSection(@NonNull String id) {
        View view = SectionsView.getInstance(mDatabase);

        return Flowable
                .<Document>create(emitter -> {
                    LiveQuery query = view.createQuery().toLiveQuery();
                    query.setKeys(Collections.singletonList(id));
                    query.addChangeListener(event -> {
                        for (QueryRow row : event.getRows()) {
                            emitter.onNext(row.getDocument());
                        }
                    });

                    emitter.setCancellable(query::stop);

                    query.run();
                }, BackpressureStrategy.LATEST)
                .map(SectionDocument::new);
    }

    @NonNull
    @Override
    public Single<Section> updateSection(@NonNull Section.Builder builder) {
        SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

        try {
            SectionDocument sectionDocument = documentBuilder.update();

            return Single.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Single.error(e);
        }
    }

    public static final class Builder {
        private Database mDatabase;

        public Repository build() {
            return new CouchbaseRepository(mDatabase);
        }

        public CouchbaseRepository.Builder database(Database database) {
            mDatabase = database;

            return this;
        }
    }
}
