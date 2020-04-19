package com.nkr.fashionita.ui.fragment.account

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.nkr.fashionita.common.toUser
import com.nkr.fashionita.model.BannerItem
import com.nkr.fashionita.model.Product
import com.nkr.fashionita.model.User
import com.nkr.fashionita.repository.CartRepoImpl
import com.nkr.fashionita.repository.FirebaseUserRepoImpl
import com.nkr.fashionita.util.*


object FirestoreUtil {


    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    private val userDataDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")


    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")




    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {

                val user =  FirebaseAuth.getInstance().currentUser



                val newUser = User(
                    user?.uid.toString(),
                    user?.displayName ?: "",
                    user?.email?:"",
                    user?.photoUrl.toString()
                )
                currentUserDocRef.set(newUser).addOnSuccessListener {

                    val map = HashMap<String, Any>()
                    map["list_size"] = 0


                    currentUserDocRef.collection(COLLECTION_USERDATA).document(COLLECTION_CARTLIST).set(map)
                    currentUserDocRef.collection(COLLECTION_USERDATA).document(COLLECTION_WISHLIST).set(map)
                    currentUserDocRef.collection(COLLECTION_USERDATA).document(COLLECTION_ORDER_LIST).set(map)



                    onComplete()
                }
            }
            else
                onComplete()
        }
    }
    fun initCurrentUserIfFirstTimeEmailPassword(user : User, onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {

                currentUserDocRef.set(user).addOnSuccessListener {

                    val map = HashMap<String, Any>()
                    map["list_size"] = 0

                    currentUserDocRef.collection(COLLECTION_USERDATA).document(COLLECTION_CARTLIST).set(map)
                    currentUserDocRef.collection(COLLECTION_USERDATA).document(COLLECTION_WISHLIST).set(map)
                    currentUserDocRef.collection(COLLECTION_USERDATA).document(
                        COLLECTION_ORDER_HISTORY).set(map)

                    onComplete()
                }
            }
            else
                onComplete()
        }
    }


    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
                .addOnSuccessListener {
                    onComplete(it.toObject(User::class.java)!!)
                }
    }



    fun getBannerImagesListener(onListen:(List<BannerItem>) -> Unit):ListenerRegistration{
        return firestoreInstance.collection("banner")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    Log.e("FIRESTORE","Something went wrong")
                    return@addSnapshotListener
                }else{
                    val bannerPhotos = mutableListOf<BannerItem>()
                    querySnapshot?.documents?.forEach {
                        Log.d("doc_snap",it.toString())

                        bannerPhotos.add(it.toObject(BannerItem::class.java)!!)

                        onListen(bannerPhotos)
                    }
                }
            }

    }


    /*fun doesWishListItemExistAlready(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {

        currentUserDocRef.collection("user_data")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }
*/

/*

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    querySnapshot!!.documents.forEach {
                        if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                            items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                    }
                    onListen(items)
                }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
                .document(otherUserId).get().addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }

                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                    val newChannel = chatChannelsCollectionRef.document()
                    newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                    currentUserDocRef
                            .collection("engagedChatChannels")
                            .document(otherUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    firestoreInstance.collection("users").document(otherUserId)
                            .collection("engagedChatChannels")
                            .document(currentUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    onComplete(newChannel.id)
                }
    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
                .orderBy("time")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    querySnapshot!!.documents.forEach {
                        if (it["type"] == MessageType.TEXT)
                            items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                        else
                            items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                        return@forEach
                    }
                    onListen(items)
                }
    }
*/

   /* fun sendMessage(message: Message, channelId: String) {
        chatChannelsCollectionRef.document(channelId)
                .collection("messages")
                .add(message)
    }

     //endregion FCM*/


    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }



    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }

    fun updateOrderHistory(prod : Product, onComplete: () -> Unit){
        //update order collection and user order document

        val user = getActiveUser()

        val order_history_map = HashMap<String,Any>()
        order_history_map["customer_uid"]= user?.uid.toString()
        order_history_map["seller_uid"] = prod.creator
        order_history_map["product_uid"] = prod.uid


        val key =  firestoreInstance.collection(COLLECTION_ORDER_HISTORY)
            .document().id

        Log.d("key_order_list",key)


        firestoreInstance.collection(COLLECTION_ORDER_HISTORY)
            .document(key)
            .set(order_history_map)
            .addOnCompleteListener {
                // update user_data/order_history/

                val order_map = HashMap<String,Any>()
                order_map[COLLECTION_ORDER_HISTORY] = FieldValue.arrayUnion(key)

                currentUserDocRef
                    .collection(COLLECTION_USERDATA)
                    .document(COLLECTION_ORDER_LIST)
                    .update(order_map)

                onComplete
            }



    }


    /**
     * if currentUser != null, return true
     */
    private fun getActiveUser(): User? {
        return FirebaseAuth.getInstance().currentUser?.toUser
    }

}