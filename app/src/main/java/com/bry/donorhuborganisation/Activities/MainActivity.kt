package com.bry.donorhuborganisation.Activities

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.bry.donorhuborganisation.Constants
import com.bry.donorhuborganisation.Fragments.Authentication.SignIn
import com.bry.donorhuborganisation.Fragments.Authentication.SignUp
import com.bry.donorhuborganisation.Fragments.Authentication.SignUpEmail
import com.bry.donorhuborganisation.Fragments.Authentication.SignUpPhone
import com.bry.donorhuborganisation.Fragments.Homepage.*
import com.bry.donorhuborganisation.Model.Donation
import com.bry.donorhuborganisation.Model.Number
import com.bry.donorhuborganisation.Model.Organisation
import com.bry.donorhuborganisation.Models.Collectors
import com.bry.donorhuborganisation.R
import com.bry.donorhuborganisation.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
        SignUp.SignUpNameInterface,
        SignUpEmail.SignUpEmailInterface,
        SignUpPhone.SignUpPhoneInterface,
        SignIn.SignInInterface,
        ViewOrganisation.ViewOrganisationInterface,
        PickOrganisation.PickOrganisationInterface,
        OrganisationPasscode.OrganisationPasscodeInterface,
        AddOrganisationMember.AddOrganisationMemberInterface,
        NewDonations.NewDonationsInterface,
        ViewDonation.ViewDonationInterface,
        SetCollectionDate.SetCollectionDateInterface
{
    val TAG = "MainActivity"
    val constants = Constants()

    val _sign_up = "_sign_up"
    val _sign_in = "_sign_in"
    val _sign_up_email = "_sign_up_email"
    val _sign_up_phone = "_sign_up_phone"
    val _new_donations = "_new_donations"
    val _view_organisation = "_view_organisation"
    val _pick_organisation = "_pick_organisation"
    val _organisation_passcode = "_organisation_passcode"
    val _add_organisation_member = "_add_organisation_member"
    val _view_donation = "_view_donation"
    val _view_set_collection_date = "_view_set_collection_date"

    private lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore
    private var donations: ArrayList<Donation> = ArrayList()
    private var organisations: ArrayList<Organisation> = ArrayList()
    private var users : ArrayList<Constants.user> = ArrayList()
    var doubleBackToExitPressedOnce: Boolean = false
    var is_loading: Boolean = false

    var has_loaded_for_first_time = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: ActionBar = supportActionBar!!
        actionBar.hide()

        if(constants.SharedPreferenceManager(applicationContext).getPersonalInfo() == null){
            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(binding.money.id, SignUp.newInstance("", ""), _sign_up).commit()
        }else{
            loadDonationsAndOrganisations()
//            openMyOrganisation()
        }


    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 1) {
            val trans = supportFragmentManager.beginTransaction()
            trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            val currentFragPos = supportFragmentManager.fragments.size - 1

            trans.remove(supportFragmentManager.fragments.get(currentFragPos))
            trans.commit()
            supportFragmentManager.popBackStack()

        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    fun hideLoadingScreen(){
        is_loading = false
        binding.loadingScreen.visibility = View.GONE
    }

    fun showLoadingScreen(){
        is_loading = true
        binding.loadingScreen.visibility = View.VISIBLE
        binding.loadingScreen.setOnTouchListener { v, _ -> true }

    }



    fun loadDonationsAndOrganisations(){
        showLoadingScreen()
        organisations.clear()
        donations.clear()
        users.clear()

        db.collection("organisations").get().addOnSuccessListener {
            if(!it.isEmpty){
                for(doc in it.documents){
                    if(doc.contains("org_obj")){
                        val org = Gson().fromJson(doc["org_obj"] as String, Organisation::class.java)
                        if(org.members==null){
                            org.members = ArrayList<String>()
                            org.admin_members =  ArrayList<String>()
                        }
                        organisations.add(org)
                    }
                }
            }
        }

        db.collection("donations").get().addOnSuccessListener {
            if(!it.isEmpty){
                for(doc in it.documents){
                    if(doc.contains("don_obj")){
                        val don = Gson().fromJson(doc["don_obj"] as String, Donation::class.java)
                        if(doc.contains("taken_down")){
                            don.is_taken_down = doc["taken_down"] as Boolean
                        }
                        donations.add(don)
                    }
                }
            }
            hideLoadingScreen()
            Toast.makeText(applicationContext, "Done loading!", Toast.LENGTH_SHORT).show()
            whenDoneLoadingData()
        }

        db.collection(constants.coll_users).get().addOnSuccessListener {
            for(user in it.documents) {
                val name = user.get("name") as String
                val email = user.get("email") as String
                val uid = user.get("uid") as String
                val user_country = user.get("user_country") as String
                val sign_up_time = user.get("sign_up_time") as Long
                val numbr = Gson().fromJson(user.get("phone_number") as String, Number::class.java)

                val us = Constants().user(numbr, email, name, sign_up_time, uid)
                users.add(us)
            }
        }
    }

    fun whenDoneLoadingData(){
        if(supportFragmentManager.findFragmentByTag(_new_donations)!=null){
            (supportFragmentManager.findFragmentByTag(_new_donations) as NewDonations).when_data_updated(donations)
        }
        if(supportFragmentManager.findFragmentByTag(_pick_organisation)!=null){
            (supportFragmentManager.findFragmentByTag(_pick_organisation) as PickOrganisation).when_data_updated(organisations)
        }

        if(!has_loaded_for_first_time){
            has_loaded_for_first_time = true
            openMyOrganisation()
        }
    }


    fun openMyOrganisation(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        var has_org = false

        for(organ in organisations){
            if(organ.members.contains(uid)) {
                val org_string = Gson().toJson(organ)
                has_org = true
                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(binding.money.id, ViewOrganisation.newInstance("", "", org_string, "")
                                , _view_organisation).commit()
            }
        }

        if(!has_org){
            val orgs = Gson().toJson(Organisation.organisation_list(organisations))
            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(binding.money.id, PickOrganisation.newInstance("", "",orgs)
                            , _pick_organisation).commit()
        }
    }


    override fun OnSignUpNameLogInInsteadSelected() {
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(binding.money.id, SignIn.newInstance("", ""), _sign_in).commit()
    }

    var name: String = ""
    var email: String = ""
    var passcode: String = ""
    lateinit var number: Number
    override fun OnSignUpNameContinueSelected(name: String) {
        this.name = name

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(binding.money.id, SignUpEmail.newInstance("", ""), _sign_up_email).commit()
    }

    override fun OnSignUpEmailContinueSelected(email: String, password: String) {
        this.email = email
        this.passcode = password

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(binding.money.id, SignUpPhone.newInstance("", ""), _sign_up_phone).commit()
    }

    var mAuth: FirebaseAuth? = null
    override fun OnSignUpPhoneContinueSelected(phoneNo: Number) {
        this.number = phoneNo

        mAuth = FirebaseAuth.getInstance()
        if(isOnline()) {
            showLoadingScreen()
            val view = this.currentFocus
            if (view != null) {
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            mAuth!!.createUserWithEmailAndPassword(email,passcode)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("main", "authentication successful")
                            createFirebaseUserProfile(task.result!!.user, email, name, number)
                        } else {
                            Snackbar.make(binding.root, resources.getString(R.string.that_didnt_work), Snackbar.LENGTH_LONG).show()
                            hideLoadingScreen()
                        }
                    }
        }else{
            Snackbar.make(binding.root, getString(R.string.please_check_on_your_internet_connection),
                    Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun createFirebaseUserProfile(user: FirebaseUser?, email: String, name: String, numbr: Number) {
        val addProfileName = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        val time = Calendar.getInstance().timeInMillis
        if (user != null) {
            user.updateProfile(addProfileName).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("main", "Created new username,")
                }
            }.addOnFailureListener {  }

            val myDataDoc = hashMapOf(
                    "email" to email,
                    "name" to name,
                    "uid" to user.uid,
                    "sign_up_time" to time,
                    "user_country" to numbr.country_name,
                    "phone_number" to Gson().toJson(numbr)
            )

            val uid = user.uid
            db.collection(constants.coll_users).document(uid)
                    .set(myDataDoc)
                    .addOnSuccessListener {
                        db.collection(constants.coll_users).document(uid)
                                .set(myDataDoc)
                                .addOnSuccessListener {
                                    Log.e(TAG, "created a new user!")

                                    constants.SharedPreferenceManager(applicationContext)
                                            .setPersonalInfo(numbr, email, name, time, user.uid)

                                    hideLoadingScreen()
                                    openMyOrganisation()
                                    loadDonationsAndOrganisations()
                                }
                    }.addOnFailureListener {  }

        }
    }

    fun isOnline(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }

    override fun OnSignInSignUpInsteadSelected() {
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(binding.money.id, SignUp.newInstance("", ""), _sign_up).commit()
    }

    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    override fun OnSubmitLogInDetails(email: String, password: String) {
        if(isOnline()){
            showLoadingScreen()
            mAuth = FirebaseAuth.getInstance()
            mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                Log.d("main", "user status changes")
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val uid = user.uid
                    db.collection(constants.coll_users).document(uid).get().addOnSuccessListener {
                        if (it.exists()) {
                            val name = it.get("name") as String
                            val sign_up_time = it.get("sign_up_time") as Long
                            val numbr = Gson().fromJson(it.get("phone_number") as String, Number::class.java)

                            constants.SharedPreferenceManager(applicationContext)
                                    .setPersonalInfo(numbr, email, name, sign_up_time, user.uid)

                            openMyOrganisation()
                            hideLoadingScreen()
                            loadDonationsAndOrganisations()
                            mAuth!!.removeAuthStateListener { mAuthListener!!}
                        }
                    }
                }
            }
            mAuth!!.addAuthStateListener(mAuthListener!!)

            val view = this.currentFocus
            if (view != null) {
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                Log.d("main", "signInWithEmail:onComplete" + task.isSuccessful)
                if (!task.isSuccessful) {
                    Snackbar.make(binding.root, "That didn't work. Please check your credentials and retry.", Snackbar.LENGTH_LONG).show()
                    if(supportFragmentManager.findFragmentByTag(_sign_in)!=null){
                        (supportFragmentManager.findFragmentByTag(_sign_in) as SignIn).didPasscodeFail()
                    }
                    mAuth!!.removeAuthStateListener { mAuthListener!!}
                    hideLoadingScreen()
                }
            }.addOnFailureListener { }
        }else{
            Snackbar.make(binding.root, getString(R.string.please_check_on_your_internet_connection), Snackbar.LENGTH_SHORT).show()
        }
    }




    override fun whenViewOrganisationViewOrganisationsDonations(organisation: Organisation) {
        val my_donations: ArrayList<Donation> = ArrayList()

        for(item in donations){
            if(item.organisation_id.equals(organisation.org_id)){
                my_donations.add(item)
            }
        }

        val dons = Gson().toJson(Donation.donation_list(my_donations))
        val orgs = Gson().toJson(organisation)

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .add(binding.money.id, NewDonations.newInstance("", "", dons,orgs)
                        , _new_donations).commit()
    }

    override fun whenNewDonationAddMember(organisation: Organisation) {
        val orgs = Gson().toJson(organisation)

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(binding.money.id, AddOrganisationMember.newInstance("", "", orgs)
                , _add_organisation_member).commit()
    }

    override fun whenPickOrganisationOrgPicked(organisation: Organisation) {
        val orgs = Gson().toJson(organisation)

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(binding.money.id, OrganisationPasscode.newInstance("", "",orgs)
                        , _organisation_passcode).commit()
    }

    override fun onOrganisationPasscodeSubmitPasscode(code: String, organisation: Organisation) {
        showLoadingScreen()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection(constants.otp_codes)
                .document(organisation.org_id)
                .collection(constants.code_instances)
                .get().addOnSuccessListener {
                    hideLoadingScreen()
                    if(!it.documents.isEmpty()){
                        var does_code_work = false
                        for(item in it.documents){
                            val item_code = item["code"] as Long
                            val item_creation_time = item["creation_time"] as Long
                            val item_organisation = item["organisation"] as String
                            val time_difference = Calendar.getInstance().timeInMillis - item_creation_time

                            if(item_code==code.toLong() && time_difference < constants.otp_expiration_time
                                    && item_organisation.equals(organisation.org_id)){
                                //code works
                                does_code_work = true
                            }
                        }
                        if(does_code_work){
                            //if password is right
                            for(item in organisations){
                                if(item.org_id.equals(organisation.org_id)){
                                    if(item.members.isEmpty()){
                                        item.admin_members.add(uid)
                                    }
                                    item.members.add(uid)
                                    update_org(item)
                                    openMyOrganisation()
                                }
                            }

                        }else{
                            //if password is wrong
                            if(supportFragmentManager.findFragmentByTag(_organisation_passcode)!=null){
                                (supportFragmentManager.findFragmentByTag(_organisation_passcode) as OrganisationPasscode).whenPasscodeFailed()
                            }
                        }
                    }else{
                        //if password is wrong
                        if(supportFragmentManager.findFragmentByTag(_organisation_passcode)!=null){
                            (supportFragmentManager.findFragmentByTag(_organisation_passcode) as OrganisationPasscode).whenPasscodeFailed()
                        }
                    }

                }
    }

    fun update_org(org: Organisation){
        val time = Calendar.getInstance().timeInMillis

        val ref = db.collection("organisations")
            .document(org.org_id)

        ref.update(mapOf(
            "org_obj" to Gson().toJson(org)
        )).addOnSuccessListener {
            Toast.makeText(applicationContext, "done!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "not updated!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun generatePasscodeClicked(organisation: Organisation, code: Long) {
        showLoadingScreen()
        db.collection(constants.otp_codes)
            .document(organisation.org_id)
            .collection(constants.code_instances)
            .document().set(hashMapOf(
                "code" to code,
                "organisation" to organisation.org_id,
                "creation_time" to Calendar.getInstance().timeInMillis
            )).addOnSuccessListener {
                hideLoadingScreen()
                Toast.makeText(applicationContext,"The password will only work for 1 min",Toast.LENGTH_SHORT).show()
                if(supportFragmentManager.findFragmentByTag(_add_organisation_member)!=null){
                    (supportFragmentManager.findFragmentByTag(_add_organisation_member) as AddOrganisationMember).isPasscodeSet()
                }
            }
    }



    override fun whenNewDonationViewDonation(donation: Donation, organisation: Organisation) {
        val don = Gson().toJson(donation)
        val org = Gson().toJson(organisation)
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, ViewDonation.newInstance("", "", org, don), _view_donation).commit()
    }

    override fun whenReloadEverything() {
        loadDonationsAndOrganisations()
    }




    override fun whenScheduleDonationPickupClicked(donation: Donation, organisation: Organisation) {
        val colls: ArrayList<Collectors> = ArrayList()
        for(usr in users){
            if(organisation.members.contains(usr.uid)){
                colls.add(Collectors(usr.name,usr.uid))
            }
        }

        val coll_s = Gson().toJson(Collectors.Collector_list(colls))
        val orgs = Gson().toJson(organisation)
        val don = Gson().toJson(donation)

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, SetCollectionDate.newInstance("", "", coll_s, orgs, don)
                , _view_set_collection_date).commit()
    }

    override fun whenFinishDonationPickupClicked(donation: Donation, organisation: Organisation) {

    }

    override fun whenFinishDonationShareLocation(
        donation: Donation,
        organisation: Organisation,
        isToShareLoc: Boolean
    ) {

    }

    override fun whenSetCollectionDateCollectorPicked(
        collector: Collectors,
        donation: Donation,
        organisation: Organisation,
        coll_time: Long
    ) {

    }


}