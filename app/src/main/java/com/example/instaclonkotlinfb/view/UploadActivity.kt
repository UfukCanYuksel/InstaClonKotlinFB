package com.example.instaclonkotlinfb.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instaclonkotlinfb.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Exception
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedPicture : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        registerLauncher()
    }

    fun upload (view : View){
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)
        selectedPicture?.let {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                // download url -> firestore
                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    auth.currentUser?.let {
                        val postMap = hashMapOf<String , Any>()
                        postMap.put("downloadUrl" , downloadUrl)
                        postMap.put("userEmail" , auth.currentUser!!.email!!)
                        postMap.put("comment" , binding.commentText.text.toString())
                        postMap.put("date" ,Timestamp.now())

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {

                            finish()

                        }.addOnFailureListener {
                            Toast.makeText(this@UploadActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
                        }
                    }


                }.addOnFailureListener {
                    Toast.makeText(this@UploadActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener {
                Toast.makeText(this@UploadActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
            }
        }


    }
    fun selectImage ( view: View){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.TIRAMISU){

            // GALERİYE ERİŞİM İZNİ VERİLMEDİYESE (!=)
            if (ContextCompat.checkSelfPermission(this ,
                    android.Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){


                // kullanıcıya hangi izni istediğimi ve mantığını gösteriyorum
                if (ActivityCompat.shouldShowRequestPermissionRationale(this , android.Manifest.permission.READ_MEDIA_IMAGES)){

                    //rationale
                    //SnackBar oluşturuyorum ve ne kadar gösterileceğini belirsiz yapıyorum (ok a basana kadar göster)
                    val snack = Snackbar.make(view , "Permission needed for gallery3", Snackbar.LENGTH_INDEFINITE)
                    //action ismi verip onClickListener da izin istiyorum
                    snack.setAction("Give Permission2",View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES )// hangi izni istediğimi verdim
                    })
                    snack.show()
                }else{  // göstermek zorunda değilsem direkt izin istiycem karar android de
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES) // hangi izni istediğimi verdim

                }

            }else{ // İZİN VERİLDİ DEMEK DİREKT GALERİYE GİDİP SEÇİLEN GÖRSELİN URI (yani yerini) alıyorum
                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI )

                //sonuç için bunu başlat
                // bu activity'i açma amacı veri alıp geri dönmek için
                activityResultLauncher.launch(intentToGallery)

            }

        }else{ // SDK 33 ÖNCESİ İÇİN
            if (ContextCompat.checkSelfPermission(this ,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.shouldShowRequestPermissionRationale(this , android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //rationale
                    val snack = Snackbar.make(view , "Permission needed for gallery3", Snackbar.LENGTH_INDEFINITE)
                    snack.setAction("Give Permission2",View.OnClickListener {  })
                    snack.show()
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE )

                }else{
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE )

                }

            }else{
                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                activityResultLauncher.launch(intentToGallery)

            }
        }


    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == RESULT_OK){
                val  intentFromResult = result.data //uri alma
                if (intentFromResult != null){
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {

                        try {
                            if (Build.VERSION.SDK_INT >=28){
                                val source = ImageDecoder.createSource( this@UploadActivity.contentResolver ,  selectedPicture!!)
                                binding.imageView.setImageURI(it)
                            }else{
                                binding.imageView.setImageURI(it)
                            }

                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }
                }
            }
        }

     permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
         if (result){// permission granted (izin verildi)
             val intentToGaleri = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
             activityResultLauncher.launch(intentToGaleri)

         }else{ // permission denied (izin verilmedi)
             Toast.makeText(this@UploadActivity , "Permission needed!1",Toast.LENGTH_LONG).show()

         }

     }

    }
}