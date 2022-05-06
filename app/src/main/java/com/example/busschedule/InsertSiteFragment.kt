package com.example.busschedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.busschedule.database.Site
import com.example.busschedule.databinding.InsertSiteFragmentBinding
import com.example.busschedule.viewmodels.SiteViewModel
import com.example.busschedule.viewmodels.SiteViewModelFactory

class InsertSiteFragment : Fragment() {

    private var _binding: InsertSiteFragmentBinding? = null

    private val binding get() = _binding!!

    private var arrondissement: Int = 0

    private val viewModel: SiteViewModel by activityViewModels {
        SiteViewModelFactory((activity?.application as SiteApplication).database.siteDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            arrondissement = it.getInt("arrondissement")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Insert New Site"

        val arrondissementEdit = binding.arrondissementEditText
        arrondissementEdit.setText(arrondissement.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = InsertSiteFragmentBinding.inflate(inflater, container, false)

        val submitButton = binding.submitSite
        submitButton.setOnClickListener {addSite()}

        return binding.root
    }

    private fun addSite() {

        val siteName = binding.siteName.text.toString()
        val arrondissement = binding.arrondissementEditText.text.toString()
        val notes = binding.notes.text.toString()
        val url = binding.url.text.toString()

        viewModel.insertSite(0, siteName, arrondissement = arrondissement.toInt(), notes, "", url)

        val action =
            InsertSiteFragmentDirections.actionInsertSiteFragmentToFullScheduleFragment(arrondissement.toInt())
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}