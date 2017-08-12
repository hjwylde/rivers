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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static com.hjwylde.rivers.util.Preconditions.requireWorkerThread;
import static java.util.Objects.requireNonNull;

public final class CouchbaseRepository implements Repository {
    @NonNull
    private final Database mDatabase;

    private CouchbaseRepository(@NonNull Database database) {
        mDatabase = requireNonNull(database);
    }

    @NonNull
    @Override
    public Single<Image> createImage(@NonNull Image.Builder builder) {
        return Single.<Image>create(emitter -> {
            requireWorkerThread();

            ImageDocument.Builder documentBuilder = ImageDocument.builder(mDatabase).copy(builder);

            try {
                ImageDocument imageDocument = documentBuilder.create();

                emitter.onSuccess(imageDocument);
            } catch (CouchbaseLiteException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.newThread());
    }

    @NonNull
    @Override
    public Single<Section> createSection(@NonNull Section.Builder builder) {
        return Single.<Section>create(emitter -> {
            requireWorkerThread();

            SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

            try {
                SectionDocument sectionDocument = documentBuilder.create();

                emitter.onSuccess(sectionDocument);
            } catch (CouchbaseLiteException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.newThread());
    }

    @NonNull
    @Override
    public Completable deleteSection(@NonNull String id) {
        return Completable.create(emitter -> {
            requireWorkerThread();

            Document document = mDatabase.getExistingDocument(id);

            try {
                if (document != null) {
                    requireTrue(document.getProperty(SectionDocument.PROPERTY_TYPE).equals(SectionDocument.TYPE));

                    document.delete();
                }

                emitter.onComplete();
            } catch (CouchbaseLiteException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.newThread());
    }

    @NonNull
    @Override
    public Maybe<Image> getImage(@NonNull String id) {
        return Maybe.<Image>create(emitter -> {
            requireWorkerThread();

            Document document = mDatabase.getExistingDocument(id);

            if (document != null) {
                ImageDocument imageDocument = new ImageDocument(document);

                emitter.onSuccess(imageDocument);
            } else {
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    @NonNull
    @Override
    public Maybe<Section> getSection(@NonNull String id) {
        return Maybe.<Section>create(emitter -> {
            requireWorkerThread();

            Document document = mDatabase.getExistingDocument(id);

            if (document != null) {
                SectionDocument sectionDocument = new SectionDocument(document);

                emitter.onSuccess(sectionDocument);
            } else {
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    @NonNull
    @Override
    public Observable<Section> getSections() {
        return Observable
                .<Document>create(emitter -> {
                    requireWorkerThread();

                    View view = SectionsView.getInstance(mDatabase);

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
                .subscribeOn(Schedulers.newThread())
                .map(SectionDocument::new);
    }

    @NonNull
    @Override
    public Flowable<Section> streamSection(@NonNull String id) {
        return Flowable
                .<Document>create(emitter -> {
                    requireWorkerThread();

                    View view = SectionsView.getInstance(mDatabase);

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
                .subscribeOn(Schedulers.newThread())
                .map(SectionDocument::new);
    }

    @NonNull
    @Override
    public Flowable<List<Section>> streamSections() {
        return Flowable
                .<List<Section>>create(emitter -> {
                    requireWorkerThread();

                    View view = SectionsView.getInstance(mDatabase);

                    LiveQuery query = view.createQuery().toLiveQuery();
                    query.addChangeListener(event -> {
                        List<Section> sections = new ArrayList<>();
                        for (QueryRow row : event.getRows()) {
                            sections.add(new SectionDocument(row.getDocument()));
                        }

                        emitter.onNext(sections);
                    });

                    emitter.setCancellable(query::stop);

                    query.run();
                }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.newThread());
    }

    @NonNull
    @Override
    public Single<Section> updateSection(@NonNull Section.Builder builder) {
        return Single.<Section>create(emitter -> {
            requireWorkerThread();

            SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

            try {
                SectionDocument sectionDocument = documentBuilder.update();

                emitter.onSuccess(sectionDocument);
            } catch (CouchbaseLiteException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.newThread());
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
