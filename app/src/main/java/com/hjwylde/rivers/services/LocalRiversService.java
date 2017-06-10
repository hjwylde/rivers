package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.queries.SectionsView;
import com.hjwylde.rivers.util.SectionQuery;

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
    public Observable<Image> createImage(@NonNull Image.Builder builder) {
        String id = UUID.randomUUID().toString();

        try {
            Image image = builder.id(id).build();

            Document document = mDatabase.getDocument(id);
            document.putProperties(image.getProperties());

            return Observable.just(image);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Section> createSection(@NonNull Section.Builder builder) {
        String id = UUID.randomUUID().toString();

        try {
            Section section = builder.id(id).build();

            Document document = mDatabase.getDocument(id);
            document.putProperties(section.getProperties());

            return Observable.just(section);
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
    public Observable<Image> getImage(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            Image image = new Image.Builder(document).build();
            return Observable.just(image);
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
                Section section = new Section.Builder(row.getDocument()).build();

                if (sectionQuery.test(section)) {
                    sections.add(section);
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
                    mSections.add(new Section.Builder(row.getDocument()).build());
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
    public Observable<Section> updateSection(@NonNull Section.Builder builder) {
        try {
            final Section section = builder.build();

            Document document = mDatabase.getExistingDocument(section.getId());
            document.update(newRevision -> {
                newRevision.setProperties(section.getProperties());

                return true;
            });

            return Observable.just(section);
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