package com.hjwylde.rivers.services;

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
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.queries.SectionsView;
import com.hjwylde.rivers.ui.util.SectionQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;

import static java.util.Objects.requireNonNull;

public final class LocalRiversService implements RiversApi {
    private final Database mDatabase;

    private LocalRiversService(Database database) {
        mDatabase = requireNonNull(database);
    }

    @NonNull
    @Override
    public Observable<ImageDocument> createImage(@NonNull ImageDocument.Builder builder) {
        String id = UUID.randomUUID().toString();

        try {
            ImageDocument imageDocument = builder.id(id).build();

            Document document = mDatabase.getDocument(id);
            document.putProperties(imageDocument.getProperties());

            return Observable.just(imageDocument);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Section> createSection(@NonNull SectionDocument.Builder builder) {
        String id = UUID.randomUUID().toString();

        try {
            SectionDocument sectionDocument = builder.id(id).build();

            Document document = mDatabase.getDocument(id);
            document.putProperties(sectionDocument.getProperties());

            return Observable.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Void> deleteSection(@NonNull Section section) {
        Document document = mDatabase.getExistingDocument(section.getId());

        try {
            if (document != null) {
                document.delete();
            }
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }

        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<ImageDocument> getImage(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            ImageDocument imageDocument = new ImageDocument.Builder(document).build();
            return Observable.just(imageDocument);
        } else {
            return Observable.empty();
        }
    }

    @NonNull
    @Override
    public Observable<List<Section>> searchSections(@NonNull String query) {
        SectionQuery sectionQuery = new SectionQuery(query);
        View view = SectionsView.getInstance(mDatabase);

        List<Section> sections = new ArrayList<>();

        try {
            QueryEnumerator result = view.createQuery().run();

            for (QueryRow row : result) {
                SectionDocument sectionDocument = new SectionDocument.Builder(row.getDocument()).build();

                if (sectionQuery.test(sectionDocument)) {
                    sections.add(sectionDocument);
                }
            }
        } catch (CouchbaseLiteException e) {
            Observable.error(e);
        }

        return Observable.just(sections);
    }

    @NonNull
    @Override
    public Observable<List<Section>> streamSections() {
        View view = SectionsView.getInstance(mDatabase);
        LiveQuery query = view.createQuery().toLiveQuery();

        QueryObserver<List<Section>> observer = new QueryObserver<List<Section>>() {
            private List<Subscriber<? super List<Section>>> mSubscribers = new ArrayList<>();

            private List<Section> mSections = new ArrayList<>();

            @Override
            public void call(Subscriber<? super List<Section>> subscriber) {
                mSubscribers.add(subscriber);

                if (!mSections.isEmpty()) {
                    subscriber.onNext(mSections);
                }
            }

            @Override
            public void changed(LiveQuery.ChangeEvent event) {
                mSections = new ArrayList<>();
                for (QueryRow row : event.getRows()) {
                    mSections.add(new SectionDocument.Builder(row.getDocument()).build());
                }

                for (Subscriber<? super List<Section>> subscriber : mSubscribers) {
                    subscriber.onNext(mSections);
                }
            }
        };

        query.addChangeListener(observer);
        query.start();

        return Observable.create(observer);
    }

    @NonNull
    @Override
    public Observable<Section> updateSection(@NonNull SectionDocument.Builder builder) {
        try {
            final SectionDocument sectionDocument = builder.build();

            Document document = mDatabase.getExistingDocument(sectionDocument.getId());
            document.update(newRevision -> {
                newRevision.setProperties(sectionDocument.getProperties());

                return true;
            });

            return Observable.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    private interface QueryObserver<T> extends LiveQuery.ChangeListener, Observable.OnSubscribe<T> {
    }

    public static final class Builder {
        private Database mDatabase;

        public RiversApi build() {
            return new LocalRiversService(mDatabase);
        }

        public LocalRiversService.Builder database(Database database) {
            mDatabase = database;

            return this;
        }
    }
}