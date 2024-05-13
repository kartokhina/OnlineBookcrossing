package com.example.onlinebookcrossing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();



        // Обработчик для кнопки добавления книги
        FloatingActionButton add = findViewById(R.id.addBook);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Создание диалогового окна для добавления книги
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_book_dialog, null );

                // Инициализация элементов интерфейса
                ImageView pic;
                TextView userName;
                TextInputLayout titleLayout, authorLayout, yearLayout, placeLayout, contactLayout, extraLayout;
                titleLayout = view1.findViewById(R.id.titleLayout);
                authorLayout = view1.findViewById(R.id.authorLayout);
                yearLayout = view1.findViewById(R.id.yearLayout);
                placeLayout = view1.findViewById(R.id.placeLayout);
                contactLayout = view1.findViewById(R.id.contactLayout);
                extraLayout = view1.findViewById(R.id.extraLayout);
                userName = view1.findViewById(R.id.UserName);


                // Получение данных из текстовых полей
                TextInputEditText titleET, authorET, yearET, placeET, contactET, extraET;
                titleET = view1.findViewById(R.id.titleET);
                authorET = view1.findViewById(R.id.authorET);
                yearET = view1.findViewById(R.id.yearET);
                placeET = view1.findViewById(R.id.placeET);
                contactET = view1.findViewById(R.id.contactET);
                extraET = view1.findViewById(R.id.extraET);
                imageView = view1.findViewById(R.id.pic);

                // Обработчик нажатия на изображение для загрузки
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePicture();
                    }
                });

                // Создание диалогового окна
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Добавить книгу").setView(view1).setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Проверка заполнения всех полей перед сохранением
                                if (Objects.requireNonNull(titleET.getText()).toString().isEmpty()){
                                    titleLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(authorET.getText()).toString().isEmpty()){
                                    authorLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(yearET.getText()).toString().isEmpty()) {
                                    yearLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(placeET.getText()).toString().isEmpty()){
                                    placeLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(contactET.getText()).toString().isEmpty()){
                                    contactLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(extraET.getText()).toString().isEmpty()) {
                                    extraLayout.setError("Поле должно быть заполнено!");
                                } else if (imageUri == null) {
                                    Toast.makeText(MainActivity.this, "Пожалуйста, выберите изображение", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Отображение прогресса сохранения данных
                                    ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                                    dialog.setMessage("Загружаем в базу данных...");
                                    dialog.show();

                                    // Сохранение изображения в Firebase Storage
                                    String imageName = "images/" + System.currentTimeMillis() + ".jpg";
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);
                                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Получение URL загруженного изображения
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Создание объекта книги с данными и URL изображения
                                                    Book book = new Book();
                                                    book.setTitle(titleET.getText().toString());
                                                    book.setAuthor(authorET.getText().toString());
                                                    book.setYear(yearET.getText().toString());
                                                    book.setPlace(placeET.getText().toString());
                                                    book.setContact(contactET.getText().toString());
                                                    book.setExtra(extraET.getText().toString());
                                                    book.setImageURL(uri.toString());

                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    String email = user.getEmail();
                                                    book.setUserName(email + "");

                                                    // Сохранение данных в базе данных Firebase
                                                    database.getReference().child("books").push().setValue(book).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            dialog.dismiss();
                                                            dialogInterface.dismiss();
                                                            Toast.makeText(MainActivity.this, "Успешно сохранено!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            dialog.dismiss();
                                                            Toast.makeText(MainActivity.this, "Произошла ошибка во время сохранения :(", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Произошла ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).setNegativeButton("Oтмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.show();
            }
        });

        // Инициализация элементов интерфейса для списка книг
        TextView empty = findViewById(R.id.empty);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        // Обработчик для получения данных из базы данных Firebase и отображения в списке
        database.getReference().child("books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Создание списка книг
                ArrayList<Book> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Book book = dataSnapshot.getValue(Book.class);
                    Objects.requireNonNull(book).setKey(dataSnapshot.getKey());
                    arrayList.add(book);
                }

                // Проверка списка на пустоту
                if (arrayList.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                // Создание адаптера для списка книг
                BookAdapter adaptor = new BookAdapter(MainActivity.this, arrayList);
                recyclerView.setAdapter(adaptor);

                // Обработчик нажатия на элемент списка книг
                adaptor.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Book book) {

                        // Проверка, является ли текущий пользователь создателем книги
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserEmail = user.getEmail();
                        String bookCreatorEmail = book.getUserName();

                        if (currentUserEmail.equals(bookCreatorEmail)) {
                            // Если пользователь создатель, открываем окно "Изменить"
                            openEditDialog(book);
                        } else {
                            // Если пользователь не создатель, открываем окно просмотра
                            openViewDialog(book);
                        }
                    }
                });
            }
            // Метод для открытия диалогового окна "Изменить"
            private void openEditDialog(Book book) {
                // Реализация диалогового окна для редактирования книги
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_book_dialog, null);
                TextView userName;
                TextInputLayout titleLayout, authorLayout, yearLayout, placeLayout, contactLayout, extraLayout;
                titleLayout = view.findViewById(R.id.titleLayout);
                authorLayout = view.findViewById(R.id.authorLayout);
                yearLayout = view.findViewById(R.id.yearLayout);
                placeLayout = view.findViewById(R.id.placeLayout);
                contactLayout = view.findViewById(R.id.contactLayout);
                extraLayout = view.findViewById(R.id.extraLayout);
                userName = view.findViewById(R.id.UserName);

                TextInputEditText titleET, authorET, yearET, placeET, contactET, extraET;
                titleET = view.findViewById(R.id.titleET);
                authorET = view.findViewById(R.id.authorET);
                yearET = view.findViewById(R.id.yearET);
                placeET = view.findViewById(R.id.placeET);
                contactET = view.findViewById(R.id.contactET);
                extraET = view.findViewById(R.id.extraET);
                imageView = view.findViewById(R.id.pic);

                // Заполнение полей данными книги
                titleET.setText(book.getTitle());
                authorET.setText(book.getAuthor());
                yearET.setText(book.getYear());
                placeET.setText(book.getPlace());
                contactET.setText(book.getContact());
                extraET.setText(book.getExtra());
                userName.setText(book.getUserName());
                Picasso.get().load(book.getImageURL()).into(imageView);

                // Обработчик нажатия на изображение для загрузки
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePicture();
                    }
                });

                // Создание диалогового окна для изменения данных

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Изменить").setView(view).setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Objects.requireNonNull(titleET.getText()).toString().isEmpty()){
                                    titleLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(authorET.getText()).toString().isEmpty()){
                                    authorLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(placeET.getText()).toString().isEmpty()){
                                    placeLayout.setError("Поле должно быть заполнено!");
                                } else if (Objects.requireNonNull(contactET.getText()).toString().isEmpty()){
                                    contactLayout.setError("Поле должно быть заполнено!");
                                } else {
                                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                    progressDialog.setMessage("Сохраняем...");
                                    progressDialog.show();
                                    // Сначала загружаем изображение в Firebase Storage
                                    String imageName = "images/" + System.currentTimeMillis() + ".jpg";
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);

                                    // Удаление старого изображения из Firebase Storage
                                    if (book.getImageURL() != null) {
                                        StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(book.getImageURL());
                                        oldImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Удаление выполнено успешно
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Ошибка при удалении старого изображения
                                            }
                                        });
                                    }

                                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Если загрузка изображения прошла успешно, получаем URL и сохраняем данные книги в базу данных
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Book updatedBook = new Book();
                                                    updatedBook.setTitle(titleET.getText().toString());
                                                    updatedBook.setAuthor(authorET.getText().toString());
                                                    updatedBook.setYear(yearET.getText().toString());
                                                    updatedBook.setPlace(placeET.getText().toString());
                                                    updatedBook.setContact(contactET.getText().toString());
                                                    updatedBook.setExtra(extraET.getText().toString());
                                                    updatedBook.setImageURL(uri.toString());
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    String email = user.getEmail();
                                                    updatedBook.setUserName(email + "");

                                                    // Сохраняем данные книги в базу данных Firebase
                                                    database.getReference().child("books").child(book.getKey()).setValue(updatedBook)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    progressDialog.dismiss();
                                                                    dialogInterface.dismiss();
                                                                    Toast.makeText(MainActivity.this, "Успешно сохранено!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(MainActivity.this, "Произошла ошибка во время сохранения :(", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Произошла ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).setNeutralButton("Закрыть", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setTitle("Удаляем...");
                                progressDialog.show();

                                // Получение ссылки на изображение в Firebase Storage
                                StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(book.getImageURL());

                                // Удаление изображения из Firebase Storage
                                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Если удаление изображения успешно, удаляем книгу из базы данных
                                        database.getReference().child("books").child(book.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Успешно удалено", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Если удаление изображения не удалось, отображаем сообщение об ошибке
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Ошибка при удалении изображения", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).create();

                alertDialog.show();
            }


            // Метод для открытия окна просмотра
            private void openViewDialog(Book book) {
                // Реализация окна просмотра книги без возможности редактирования

                // Создание диалогового окна для просмотра данных книги
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_book_dialog, null);
                TextView userName, titleET, authorET, yearET, placeET, contactET, extraET;
                titleET = view.findViewById(R.id.titleET);
                authorET = view.findViewById(R.id.authorET);
                yearET = view.findViewById(R.id.yearET);
                placeET = view.findViewById(R.id.placeET);
                contactET = view.findViewById(R.id.contactET);
                extraET = view.findViewById(R.id.extraET);
                userName = view.findViewById(R.id.UserName);
                imageView = view.findViewById(R.id.pic);

                // Заполнение полей данными книги
                titleET.setText(book.getTitle());
                authorET.setText(book.getAuthor());
                yearET.setText(book.getYear());
                placeET.setText(book.getPlace());
                contactET.setText(book.getContact());
                extraET.setText(book.getExtra());
                userName.setText(book.getUserName());
                Picasso.get().load(book.getImageURL()).into(imageView);

                contactET.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Contact", contactET.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "Контактная информация скопирована", Toast.LENGTH_SHORT).show();
                    }
                });

                // Создание диалогового окна для просмотра данных
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Просмотр").setView(view).setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработчик ошибок при получении данных из базы данных Firebase
            }
        });

    }

    // Метод для выбора изображения из галереи
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }


}