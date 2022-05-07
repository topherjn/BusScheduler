package com.example.busschedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.example.busschedule.databinding.InsertSiteFragmentBinding
import com.example.busschedule.viewmodels.SiteViewModel
import com.example.busschedule.viewmodels.SiteViewModelFactory
import kotlinx.coroutines.launch

class InsertSiteFragment : Fragment() {

    private var _binding: InsertSiteFragmentBinding? = null

    private val binding get() = _binding!!

    private var siteId: Int = 0

    private val viewModel: SiteViewModel by activityViewModels {
        SiteViewModelFactory((activity?.application as SiteApplication).database.siteDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            siteId = it.getInt("arrondissement")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Insert New Site"

        lifecycle.coroutineScope.launch {
            viewModel.getSite(siteId = siteId).collect() {
                val site = it.get(0)

                val siteNameEdit = binding.siteName
                siteNameEdit.setText(site.siteName)

                val arrondissementEdit = binding.arrondissementEditText
                arrondissementEdit.setText(site.arrondissement.toString())

                val siteUrlEdit = binding.url
                siteUrlEdit.setText(site.url)

                val notesEditText = binding.notes
                notesEditText.setText(site.notes)

                val viewMapButton = binding.viewMapButton
                viewMapButton.setOnClickListener { viewMap(site.url!!) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = InsertSiteFragmentBinding.inflate(inflater, container, false)

        val submitButton = binding.editSiteButton
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

    private fun viewMap(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}