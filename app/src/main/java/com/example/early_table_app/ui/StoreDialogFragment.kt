package com.example.early_table_waiting.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.early_table_waiting.R
import com.example.early_table_waiting.StoreDataHolder
import com.example.early_table_waiting.WaitingSelectionActivity
import com.example.early_table_waiting.model.Store
import com.example.early_table_waiting.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreDialogFragment : DialogFragment() {

    interface StoreDialogListener {
        fun onConfirm()
    }

    private var listener: StoreDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? StoreDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val store: Store? = arguments?.getParcelable("store")

        val message = if (store != null) {
            getString(R.string.waiting_confirmation, store.name)
        } else {
            "가게 이름이 없습니다."
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("웨이팅 설정")
            .setMessage(message)
            .setPositiveButton("예") { _, _ ->
                store?.let {
                    StoreDataHolder.selectedStore = it
                    val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putLong("store_id", it.id) // Store ID 저장
                        apply()
                    }
                    val intent = Intent(requireContext(), WaitingSelectionActivity::class.java)
                    startActivity(intent)
                }
            }
            .setNegativeButton("아니요", null)
            .create()
    }

    companion object {
        fun newInstance(store: Store): StoreDialogFragment {
            val fragment = StoreDialogFragment()
            val args = Bundle()
            args.putParcelable("store", store) // Store 객체를 Parcelize로 전달
            fragment.arguments = args
            return fragment
        }
    }


}
